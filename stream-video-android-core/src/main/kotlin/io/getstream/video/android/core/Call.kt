/*
 * Copyright (c) 2014-2023 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.video.android.core

import android.content.Intent
import android.graphics.Bitmap
import androidx.annotation.VisibleForTesting
import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.Result.Failure
import io.getstream.result.Result.Success
import io.getstream.video.android.core.call.RtcSession
import io.getstream.video.android.core.call.audio.AudioFilter
import io.getstream.video.android.core.call.utils.SoundInputProcessor
import io.getstream.video.android.core.call.video.VideoFilter
import io.getstream.video.android.core.call.video.YuvFrame
import io.getstream.video.android.core.events.GoAwayEvent
import io.getstream.video.android.core.events.VideoEventListener
import io.getstream.video.android.core.internal.InternalStreamVideoApi
import io.getstream.video.android.core.model.MuteUsersData
import io.getstream.video.android.core.model.QueriedMembers
import io.getstream.video.android.core.model.SortField
import io.getstream.video.android.core.model.UpdateUserPermissionsData
import io.getstream.video.android.core.model.VideoTrack
import io.getstream.video.android.core.model.toIceServer
import io.getstream.video.android.core.socket.SocketState
import io.getstream.video.android.core.utils.RampValueUpAndDownHelper
import io.getstream.video.android.core.utils.toQueriedMembers
import io.getstream.video.android.model.User
import io.getstream.webrtc.android.ui.VideoTextureViewRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.openapitools.client.models.AcceptCallResponse
import org.openapitools.client.models.AudioSettings
import org.openapitools.client.models.BlockUserResponse
import org.openapitools.client.models.CallSettingsRequest
import org.openapitools.client.models.CallSettingsResponse
import org.openapitools.client.models.GetCallResponse
import org.openapitools.client.models.GetOrCreateCallResponse
import org.openapitools.client.models.GoLiveResponse
import org.openapitools.client.models.JoinCallResponse
import org.openapitools.client.models.ListRecordingsResponse
import org.openapitools.client.models.MemberRequest
import org.openapitools.client.models.MuteUsersResponse
import org.openapitools.client.models.OwnCapability
import org.openapitools.client.models.PinResponse
import org.openapitools.client.models.RejectCallResponse
import org.openapitools.client.models.SendEventResponse
import org.openapitools.client.models.SendReactionResponse
import org.openapitools.client.models.StopLiveResponse
import org.openapitools.client.models.UnpinResponse
import org.openapitools.client.models.UpdateCallMembersRequest
import org.openapitools.client.models.UpdateCallMembersResponse
import org.openapitools.client.models.UpdateCallRequest
import org.openapitools.client.models.UpdateCallResponse
import org.openapitools.client.models.UpdateUserPermissionsResponse
import org.openapitools.client.models.VideoEvent
import org.openapitools.client.models.VideoSettings
import org.threeten.bp.OffsetDateTime
import org.webrtc.RendererCommon
import org.webrtc.VideoSink
import org.webrtc.audio.JavaAudioDeviceModule.AudioSamples
import stream.video.sfu.models.TrackType
import stream.video.sfu.models.VideoDimension
import kotlin.coroutines.resume

/**
 * How long do we keep trying to make a full-reconnect (once the SFU signalling WS went down)
 */
const val sfuReconnectTimeoutMillis = 30_000

/**
 * The call class gives you access to all call level API calls
 *
 * @sample
 *
 * val call = client.call("default", "123")
 * val result = call.create() // update, get etc.
 * // join the call and get audio/video
 * val result = call.join()
 *
 */
public class Call(
    internal val client: StreamVideo,
    val type: String,
    val id: String,
    val user: User,
) {
    private var statsGatheringJob: Job? = null
    internal var location: String? = null

    private var subscriptions = mutableSetOf<EventSubscription>()

    internal val clientImpl = client as StreamVideoImpl
    private val logger by taggedLogger("Call")

    private val supervisorJob = SupervisorJob()
    private val scope = CoroutineScope(clientImpl.scope.coroutineContext + supervisorJob)

    /** The call state contains all state such as the participant list, reactions etc */
    val state = CallState(client, this, user, scope)

    val sessionId by lazy { clientImpl.sessionId }
    private val network by lazy { clientImpl.connectionModule.networkStateProvider }

    /** Camera gives you access to the local camera */
    val camera by lazy { mediaManager.camera }
    val microphone by lazy { mediaManager.microphone }
    val speaker by lazy { mediaManager.speaker }
    val screenShare by lazy { mediaManager.screenShare }

    /** The cid is type:id */
    val cid = "$type:$id"

    /**
     * Set a custom [VideoFilter] that will be applied to the video stream coming from your device.
     */
    var videoFilter: VideoFilter? = null

    /**
     * Set a custom [AudioFilter] that will be applied to the audio stream recorded on your device.
     */
    var audioFilter: AudioFilter? = null

    /**
     * Called by the [CallHealthMonitor] when the ICE restarts failed after
     * several retries. At this point we can do a full reconnect.
     */
    private val onIceRecoveryFailed = {
        scope.launch {
            handleSignalChannelDisconnect(false)
        }
        Unit
    }

    val monitor = CallHealthMonitor(this, scope, onIceRecoveryFailed)

    private val soundInputProcessor = SoundInputProcessor(thresholdCrossedCallback = {
        if (!microphone.isEnabled.value) {
            state.markSpeakingAsMuted()
        }
    })
    private val audioLevelOutputHelper = RampValueUpAndDownHelper()

    /**
     * This returns the local microphone volume level. The audio volume is a linear
     * value between 0 (no sound) and 1 (maximum volume). This is not a raw output -
     * it is a smoothed-out volume level that gradually goes to the highest measured level
     * and will then gradually over 250ms return back to 0 or next measured value. This value
     * can be used directly in your UI for displaying a volume/speaking indicator for the local
     * participant.
     * Note: Doesn't return any values until the session is established!
     */
    val localMicrophoneAudioLevel: StateFlow<Float> = audioLevelOutputHelper.currentLevel

    /**
     * Contains stats events for observation.
     */
    @InternalStreamVideoApi
    val statsReport: MutableStateFlow<CallStatsReport?> = MutableStateFlow(null)

    /**
     * Time (in millis) when the full reconnection flow started. Will be null again once
     * the reconnection flow ends (success or failure)
     */
    private var sfuSocketReconnectionTime: Long? = null

    /**
     * Call has been left and the object is cleaned up and destroyed.
     */
    private var isDestroyed = false

    /** Session handles all real time communication for video and audio */
    internal var session: RtcSession? = null
    internal val mediaManager by lazy {
        if (testInstanceProvider.mediaManagerCreator != null) {
            testInstanceProvider.mediaManagerCreator!!.invoke()
        } else {
            MediaManagerImpl(
                clientImpl.context,
                this,
                scope,
                clientImpl.peerConnectionFactory.eglBase.eglBaseContext,
            )
        }
    }

    init {
        scope.launch {
            soundInputProcessor.currentAudioLevel.collect {
                audioLevelOutputHelper.rampToValue(it)
            }
        }
    }

    /** Basic crud operations */
    suspend fun get(): Result<GetCallResponse> {
        val response = clientImpl.getCall(type, id)
        response.onSuccess {
            state.updateFromResponse(it)
        }
        return response
    }

    /** Create a call. You can create a call client side, many apps prefer to do this server side though */
    suspend fun create(
        memberIds: List<String>? = null,
        members: List<MemberRequest>? = null,
        custom: Map<String, Any>? = null,
        settings: CallSettingsRequest? = null,
        startsAt: OffsetDateTime? = null,
        team: String? = null,
        ring: Boolean = false,
        notify: Boolean = false,
    ): Result<GetOrCreateCallResponse> {
        val response = if (members != null) {
            clientImpl.getOrCreateCallFullMembers(
                type = type,
                id = id,
                members = members,
                custom = custom,
                settingsOverride = settings,
                startsAt = startsAt,
                team = team,
                ring = ring,
                notify = notify,
            )
        } else {
            clientImpl.getOrCreateCall(
                type = type,
                id = id,
                memberIds = memberIds,
                custom = custom,
                settingsOverride = settings,
                startsAt = startsAt,
                team = team,
                ring = ring,
                notify = notify,
            )
        }

        response.onSuccess {
            state.updateFromResponse(it)
            if (ring) {
                client.state.addRingingCall(this)
            }
        }
        return response
    }

    /** Update a call */
    suspend fun update(
        custom: Map<String, Any>? = null,
        settingsOverride: CallSettingsRequest? = null,
        startsAt: OffsetDateTime? = null,
    ): Result<UpdateCallResponse> {
        val request = UpdateCallRequest(
            custom = custom,
            settingsOverride = settingsOverride,
            startsAt = startsAt,
        )
        val response = clientImpl.updateCall(type, id, request)
        response.onSuccess {
            state.updateFromResponse(it)
        }
        return response
    }

    suspend fun join(
        create: Boolean = false,
        createOptions: CreateCallOptions? = null,
        ring: Boolean = false,
        notify: Boolean = false,
    ): Result<RtcSession> {
        // if we are a guest user, make sure we wait for the token before running the join flow
        clientImpl.guestUserJob?.await()
        // the join flow should retry up to 3 times
        // if the error is not permanent
        // and fail immediately on permanent errors
        state._connection.value = RealtimeConnection.InProgress
        var retryCount = 0

        var result: Result<RtcSession>

        while (retryCount < 3) {
            result = _join(create, createOptions, ring, notify)
            if (result is Success) {
                // we initialise the camera, mic and other according to local + backend settings
                // only when the call is joined to make sure we don't switch and override
                // the settings during a call.
                val settings = state.settings.value
                if (settings != null) {
                    updateMediaManagerFromSettings(settings)
                } else {
                    logger.w {
                        "[join] Call settings were null - this should never happen after a call" +
                            "is joined. MediaManager will not be initialised with server settings."
                    }
                }
                return result
            }
            if (result is Failure) {
                session = null
                logger.e { "Join failed with error $result" }
                if (isPermanentError(result.value)) {
                    state._connection.value = RealtimeConnection.Failed(result.value)
                    return result
                } else {
                    retryCount += 1
                }
            }
            delay(retryCount - 1 * 1000L)
        }
        return Failure(value = Error.GenericError("Join failed after 3 retries"))
    }

    internal fun isPermanentError(error: Any): Boolean {
        return true
    }

    internal suspend fun _join(
        create: Boolean = false,
        createOptions: CreateCallOptions? = null,
        ring: Boolean = false,
        notify: Boolean = false,
    ): Result<RtcSession> {
        if (session != null) {
            throw IllegalStateException(
                "Call $cid has already been joined. Please use call.leave before joining it again",
            )
        }

        // step 1. call the join endpoint to get a list of SFUs
        val timer = clientImpl.debugInfo.trackTime("call.join")

        val locationResult = clientImpl.getCachedLocation()
        if (locationResult !is Success) {
            return locationResult as Failure
        }
        location = locationResult.value
        timer.split("location found")

        val options = createOptions
            ?: if (create) {
                CreateCallOptions()
            } else {
                null
            }
        val result = joinRequest(options, locationResult.value, ring = ring, notify = notify)

        if (result !is Success) {
            return result as Failure
        }
        val sfuToken = result.value.credentials.token
        val sfuUrl = clientImpl.testSfuAddress ?: result.value.credentials.server.url
        val iceServers = result.value.credentials.iceServers.map { it.toIceServer() }
        timer.split("join request completed")

        session = if (testInstanceProvider.rtcSessionCreator != null) {
            testInstanceProvider.rtcSessionCreator!!.invoke()
        } else {
            RtcSession(
                client = client,
                call = this,
                sfuUrl = sfuUrl,
                sfuToken = sfuToken,
                connectionModule = (client as StreamVideoImpl).connectionModule,
                remoteIceServers = iceServers,
                onMigrationCompleted = {
                    state._connection.value = RealtimeConnection.Connected
                    monitor.check()
                },
            )
        }

        session?.let {
            state._connection.value = RealtimeConnection.Joined(it)
        }
        timer.split("rtc session init")

        session?.connect()

        timer.split("rtc connect completed")

        scope.launch {
            // wait for the first stream to be added
            session?.let { rtcSession ->
                val mainRtcSession = rtcSession.lastVideoStreamAdded.filter { it != null }.first()
                timer.finish("stream added, rtc completed, ready to display video $mainRtcSession")
            }
        }

        monitor.start()

        val statsGatheringInterval = 5000L

        statsGatheringJob = scope.launch {
            // wait a bit before we capture stats
            delay(statsGatheringInterval)

            while (isActive) {
                delay(statsGatheringInterval)

                val publisherStats = session?.publisher?.getStats()
                val subscriberStats = session?.subscriber?.getStats()
                state.stats.updateFromRTCStats(publisherStats, isPublisher = true)
                state.stats.updateFromRTCStats(subscriberStats, isPublisher = false)
                state.stats.updateLocalStats()
                statsReport.value = CallStatsReport(
                    publisher = publisherStats,
                    subscriber = subscriberStats,
                )
            }
        }

        client.state.setActiveCall(this)

        // listen to Signal WS
        scope.launch {
            session?.let {
                it.sfuSocketState.collect { sfuSocketState ->
                    if (sfuSocketState is SocketState.DisconnectedPermanently) {
                        handleSignalChannelDisconnect(isRetry = false)
                    }
                }
            }
        }

        timer.finish()

        return Success(value = session!!)
    }

    private suspend fun handleSignalChannelDisconnect(isRetry: Boolean) {
        // Prevent multiple starts of the reconnect flow. For the start call
        // first check if sfuSocketReconnectionTime isn't already set - if yes
        // then we are already doing a full reconnect
        if (state._connection.value == RealtimeConnection.Migrating) {
            logger.d { "Skipping disconnected channel event - we are migrating" }
            return
        }

        if (!isRetry && sfuSocketReconnectionTime != null) {
            logger.d { "[handleSignalChannelDisconnect] Already doing a full reconnect cycle - ignoring call" }
            return
        }

        if (!isRetry) {
            state._connection.value = RealtimeConnection.Reconnecting

            if (sfuSocketReconnectionTime == null) {
                sfuSocketReconnectionTime = System.currentTimeMillis()
            }

            // We were not able to restore the SFU peer connection in time
            if (System.currentTimeMillis() - (
                    sfuSocketReconnectionTime
                        ?: System.currentTimeMillis()
                    ) > sfuReconnectTimeoutMillis
            ) {
                leave(Error("Failed to do a full reconnect - connection issue?"))
                return
            }

            // Clean up the existing RtcSession
            session?.cleanup()
            session = null

            // Wait a little for clean-up
            delay(250)

            // Re-join the call
            val result = _join()
            if (result.isFailure) {
                // keep trying until timeout
                handleSignalChannelDisconnect(isRetry = true)
            } else {
                sfuSocketReconnectionTime = null
            }
        }
    }

    suspend fun sendStats(data: Map<String, Any>) {
        return clientImpl.sendStats(type, id, data)
    }

    suspend fun switchSfu() {
        state._connection.value = RealtimeConnection.Migrating

        location?.let {
            val joinResponse = joinRequest(location = it, migratingFrom = session?.sfuUrl)

            if (joinResponse is Success) {
                // switch to the new SFU
                val cred = joinResponse.value.credentials
                logger.i { "Switching SFU from ${session?.sfuUrl} to ${cred.server.url}" }
                val iceServers = cred.iceServers.map { it.toIceServer() }

                session?.switchSfu(cred.server.edgeName, cred.server.url, cred.token, iceServers, failedToSwitch = {
                    logger.e {
                        "[switchSfu] Failed to connect to new SFU during migration. Reverting to full reconnect"
                    }
                    state._connection.value = RealtimeConnection.Reconnecting
                })
            } else {
                logger.e {
                    "[switchSfu] Failed to get a join response during " +
                        "migration - falling back to reconnect. Error ${joinResponse.errorOrNull()}"
                }
                state._connection.value = RealtimeConnection.Reconnecting
            }
        }
    }

    suspend fun reconnect(forceRestart: Boolean) {
        // mark us as reconnecting
        val connectionState = state._connection.value

        if (connectionState is RealtimeConnection.Joined || connectionState == RealtimeConnection.Connected) {
            state._connection.value = RealtimeConnection.Reconnecting
        }

        // see if we are online before attempting to reconnect
        val online = network.isConnected()

        if (online) {
            // start by restarting ice connections
            session?.reconnect(forceRestart = forceRestart)
        }
    }

    /** Leave the call, but don't end it for other users */
    fun leave() {
        leave(disconnectionReason = null)
    }

    private fun leave(disconnectionReason: Throwable?) {
        if (isDestroyed) {
            logger.w { "[leave] Call already destroyed, ignoring" }
            return
        }
        isDestroyed = true

        sfuSocketReconnectionTime = null
        state._connection.value = if (disconnectionReason != null) {
            RealtimeConnection.Failed(disconnectionReason)
        } else {
            RealtimeConnection.Disconnected
        }
        stopScreenSharing()
        client.state.removeActiveCall()
        client.state.removeRingingCall()
        (client as StreamVideoImpl).onCallCleanUp(this)
        camera.disable()
        microphone.disable()
        cleanup()
    }

    /** ends the call for yourself as well as other users */
    suspend fun end(): Result<Unit> {
        // end the call for everyone
        val result = clientImpl.endCall(type, id)
        // cleanup
        leave()
        return result
    }

    suspend fun pinForEveryone(sessionId: String, userId: String): Result<PinResponse> {
        return clientImpl.pinForEveryone(type, id, sessionId, userId)
    }

    suspend fun unpinForEveryone(sessionId: String, userId: String): Result<UnpinResponse> {
        return clientImpl.unpinForEveryone(type, id, sessionId, userId)
    }

    suspend fun sendReaction(
        type: String,
        emoji: String? = null,
        custom: Map<String, Any>? = null,
    ): Result<SendReactionResponse> {
        return clientImpl.sendReaction(this.type, id, type, emoji, custom)
    }

    suspend fun queryMembers(
        filter: Map<String, Any>,
        sort: List<SortField> = mutableListOf(SortField.Desc("created_at")),
        limit: Int = 25,
        prev: String? = null,
        next: String? = null,
    ): Result<QueriedMembers> {
        return clientImpl.queryMembersInternal(
            type = type,
            id = id,
            filter = filter,
            sort = sort,
            prev = prev,
            next = next,
            limit = limit,
        ).onSuccess { state.updateFromResponse(it) }.map { it.toQueriedMembers() }
    }

    suspend fun muteAllUsers(
        audio: Boolean = true,
        video: Boolean = false,
        screenShare: Boolean = false,
    ): Result<MuteUsersResponse> {
        val request = MuteUsersData(
            muteAllUsers = true,
            audio = audio,
            video = video,
            screenShare = screenShare,
        )
        return clientImpl.muteUsers(type, id, request)
    }

    fun setVisibility(sessionId: String, trackType: TrackType, visible: Boolean) {
        session?.updateTrackDimensions(sessionId, trackType, visible)
    }

    fun handleEvent(event: VideoEvent) {
        logger.i { "[call handleEvent] #sfu; event: $event" }

        when (event) {
            is GoAwayEvent ->
                scope.launch {
                    handleSessionMigrationEvent()
                }
        }
    }

    private suspend fun handleSessionMigrationEvent() {
        logger.d { "[handleSessionMigrationEvent] Received goAway event - starting migration" }
        switchSfu()
    }

    // TODO: review this
    /**
     * Perhaps it would be nicer to have an interface. Any UI elements that renders video should implement it
     *
     * And call a callback for
     * - visible/hidden
     * - resolution changes
     */
    public fun initRenderer(
        videoRenderer: VideoTextureViewRenderer,
        sessionId: String,
        trackType: TrackType,
        onRendered: (VideoTextureViewRenderer) -> Unit = {},
    ) {
        logger.d { "[initRenderer] #sfu; sessionId: $sessionId" }

        // Note this comes from peerConnectionFactory.eglBase
        videoRenderer.init(
            clientImpl.peerConnectionFactory.eglBase.eglBaseContext,
            object : RendererCommon.RendererEvents {
                override fun onFirstFrameRendered() {
                    logger.d { "[initRenderer.onFirstFrameRendered] #sfu; sessionId: $sessionId" }
                    if (trackType != TrackType.TRACK_TYPE_SCREEN_SHARE) {
                        session?.updateTrackDimensions(
                            sessionId,
                            trackType,
                            true,
                            VideoDimension(
                                videoRenderer.measuredWidth,
                                videoRenderer.measuredHeight,
                            ),
                        )
                    }
                    onRendered(videoRenderer)
                }

                override fun onFrameResolutionChanged(p0: Int, p1: Int, p2: Int) {
                    logger.d { "[initRenderer.onFrameResolutionChanged] #sfu; sessionId: $sessionId" }

                    if (trackType != TrackType.TRACK_TYPE_SCREEN_SHARE) {
                        session?.updateTrackDimensions(
                            sessionId,
                            trackType,
                            true,
                            VideoDimension(
                                videoRenderer.measuredWidth,
                                videoRenderer.measuredHeight,
                            ),
                        )
                    }
                }
            },
        )
    }

    suspend fun goLive(
        startHls: Boolean = false,
        startRecording: Boolean = false,
        startTranscription: Boolean = false,
    ): Result<GoLiveResponse> {
        val result = clientImpl.goLive(
            type = type,
            id = id,
            startHls = startHls,
            startRecording = startRecording,
            startTranscription = startTranscription,
        )
        result.onSuccess { state.updateFromResponse(it) }

        return result
    }

    suspend fun stopLive(): Result<StopLiveResponse> {
        val result = clientImpl.stopLive(type, id)
        result.onSuccess { state.updateFromResponse(it) }
        return result
    }

    suspend fun sendCustomEvent(data: Map<String, Any>): Result<SendEventResponse> {
        return clientImpl.sendCustomEvent(this.type, this.id, data)
    }

    /** Permissions */
    suspend fun requestPermissions(vararg permission: String): Result<Unit> {
        return clientImpl.requestPermissions(type, id, permission.toList())
    }

    suspend fun startRecording(): Result<Any> {
        return clientImpl.startRecording(type, id)
    }

    suspend fun stopRecording(): Result<Any> {
        return clientImpl.stopRecording(type, id)
    }

    /**
     * User needs to have [OwnCapability.Screenshare] capability in order to start screen
     * sharing.
     *
     * @param mediaProjectionPermissionResultData - intent data returned from the
     * activity result after asking for screen sharing permission by launching
     * MediaProjectionManager.createScreenCaptureIntent().
     * See https://developer.android.com/guide/topics/large-screens/media-projection#recommended_approach
     */
    fun startScreenSharing(mediaProjectionPermissionResultData: Intent) {
        if (state.ownCapabilities.value.contains(OwnCapability.Screenshare)) {
            session?.setScreenShareTrack()
            screenShare.enable(mediaProjectionPermissionResultData)
        } else {
            logger.w { "Can't start screen sharing - user doesn't have wnCapability.Screenshare permission" }
        }
    }

    fun stopScreenSharing() {
        screenShare.disable(fromUser = true)
    }

    suspend fun startHLS(): Result<Any> {
        return clientImpl.startBroadcasting(type, id)
            .onSuccess {
                state.updateFromResponse(it)
            }
    }

    suspend fun stopHLS(): Result<Any> {
        return clientImpl.stopBroadcasting(type, id)
    }

    public fun subscribeFor(
        vararg eventTypes: Class<out VideoEvent>,
        listener: VideoEventListener<VideoEvent>,
    ): EventSubscription {
        val filter = { event: VideoEvent ->
            eventTypes.any { type -> type.isInstance(event) }
        }
        val sub = EventSubscription(listener, filter)
        subscriptions.add(sub)
        return sub
    }

    public fun subscribe(
        listener: VideoEventListener<VideoEvent>,
    ): EventSubscription {
        val sub = EventSubscription(listener)
        subscriptions.add(sub)
        return sub
    }

    public suspend fun blockUser(userId: String): Result<BlockUserResponse> {
        return clientImpl.blockUser(type, id, userId)
    }

    // TODO: add removeMember (single)

    public suspend fun removeMembers(userIds: List<String>): Result<UpdateCallMembersResponse> {
        val request = UpdateCallMembersRequest(removeMembers = userIds)
        return clientImpl.updateMembers(type, id, request)
    }

    public suspend fun grantPermissions(
        userId: String,
        permissions: List<String>,
    ): Result<UpdateUserPermissionsResponse> {
        val request = UpdateUserPermissionsData(
            userId = userId,
            grantedPermissions = permissions,
        )
        return clientImpl.updateUserPermissions(type, id, request)
    }

    public suspend fun revokePermissions(
        userId: String,
        permissions: List<String>,
    ): Result<UpdateUserPermissionsResponse> {
        val request = UpdateUserPermissionsData(
            userId = userId,
            revokedPermissions = permissions,
        )
        return clientImpl.updateUserPermissions(type, id, request)
    }

    public suspend fun updateMembers(memberRequests: List<MemberRequest>): Result<UpdateCallMembersResponse> {
        val request = UpdateCallMembersRequest(updateMembers = memberRequests)
        return clientImpl.updateMembers(type, id, request)
    }

    fun fireEvent(event: VideoEvent) {
        subscriptions.forEach { sub ->
            if (!sub.isDisposed) {
                // subs without filters should always fire
                if (sub.filter == null) {
                    sub.listener.onEvent(event)
                }

                // if there is a filter, check it and fire if it matches
                sub.filter?.let {
                    if (it.invoke(event)) {
                        sub.listener.onEvent(event)
                    }
                }
            }
        }
    }

    private fun updateMediaManagerFromSettings(callSettings: CallSettingsResponse) {
        // Speaker
        if (speaker.status.value is DeviceStatus.NotSelected) {
            val enableSpeaker = if (callSettings.video.cameraDefaultOn || camera.status.value is DeviceStatus.Enabled) {
                // if camera is enabled then enable speaker. Eventually this should
                // be a new audio.defaultDevice setting returned from backend
                true
            } else {
                callSettings.audio.defaultDevice == AudioSettings.DefaultDevice.Speaker
            }
            speaker.setEnabled(enableSpeaker)
        }

        // Camera
        if (camera.status.value is DeviceStatus.NotSelected) {
            val defaultDirection =
                if (callSettings.video.cameraFacing == VideoSettings.CameraFacing.Front) {
                    CameraDirection.Front
                } else {
                    CameraDirection.Back
                }
            camera.setDirection(defaultDirection)
            camera.setEnabled(callSettings.video.cameraDefaultOn)
        }

        // Mic
        if (microphone.status.value == DeviceStatus.NotSelected) {
            val enabled = callSettings.audio.micDefaultOn
            microphone.setEnabled(enabled)
        }
    }

    suspend fun listRecordings(): Result<ListRecordingsResponse> {
        return clientImpl.listRecordings(type, id, "what")
    }

    suspend fun muteUser(
        userId: String,
        audio: Boolean = true,
        video: Boolean = false,
        screenShare: Boolean = false,
    ): Result<MuteUsersResponse> {
        val request = MuteUsersData(
            users = listOf(userId),
            muteAllUsers = false,
            audio = audio,
            video = video,
            screenShare = screenShare,
        )
        return clientImpl.muteUsers(type, id, request)
    }

    suspend fun muteUsers(
        userIds: List<String>,
        audio: Boolean = true,
        video: Boolean = false,
        screenShare: Boolean = false,
    ): Result<MuteUsersResponse> {
        val request = MuteUsersData(
            users = userIds,
            muteAllUsers = false,
            audio = audio,
            video = video,
            screenShare = screenShare,
        )
        return clientImpl.muteUsers(type, id, request)
    }

    @VisibleForTesting
    internal suspend fun joinRequest(
        create: CreateCallOptions? = null,
        location: String,
        migratingFrom: String? = null,
        ring: Boolean = false,
        notify: Boolean = false,
    ): Result<JoinCallResponse> {
        val result = clientImpl.joinCall(
            type, id,
            create = create != null,
            members = create?.memberRequestsFromIds(),
            custom = create?.custom,
            settingsOverride = create?.settings,
            startsAt = create?.startsAt,
            team = create?.team,
            ring = ring,
            notify = notify,
            location = location,
            migratingFrom = migratingFrom,
        )
        result.onSuccess {
            state.updateFromResponse(it)
        }
        return result
    }

    fun cleanup() {
        monitor.stop()
        session?.cleanup()
        supervisorJob.cancel()
        statsGatheringJob?.cancel()
        mediaManager.cleanup()
        session = null
    }

    suspend fun ring(): Result<GetCallResponse> {
        return clientImpl.ring(type, id)
    }

    suspend fun notify(): Result<GetCallResponse> {
        return clientImpl.notify(type, id)
    }

    suspend fun accept(): Result<AcceptCallResponse> {
        return clientImpl.accept(type, id)
    }

    suspend fun reject(): Result<RejectCallResponse> {
        return clientImpl.reject(type, id)
    }

    fun processAudioSample(audioSample: AudioSamples) {
        soundInputProcessor.processSoundInput(audioSample.data)
    }

    suspend fun takeScreenshot(track: VideoTrack): Bitmap? {
        return suspendCancellableCoroutine { continuation ->
            var screenshotSink: VideoSink? = null
            screenshotSink = VideoSink {
                // make sure we stop after first frame is delivered
                if (!continuation.isActive) {
                    return@VideoSink
                }
                it.retain()
                val bitmap = YuvFrame.bitmapFromVideoFrame(it)
                it.release()

                // This has to be launched asynchronously - removing the sink on the
                // same thread as the videoframe is delivered will lead to a deadlock
                // (needs investigation why)
                scope.launch {
                    track.video.removeSink(screenshotSink)
                }
                continuation.resume(bitmap)
            }

            track.video.addSink(screenshotSink)
        }
    }

    fun isPinnedParticipant(sessionId: String): Boolean = state.pinnedParticipants.value.containsKey(
        sessionId,
    )

    fun isServerPin(sessionId: String): Boolean = state._serverPins.value.containsKey(sessionId)

    fun isLocalPin(sessionId: String): Boolean = state._localPins.value.containsKey(sessionId)

    fun hasCapability(vararg capability: OwnCapability): Boolean {
        val elements = capability.toList()
        return state.ownCapabilities.value.containsAll(elements)
    }

    @InternalStreamVideoApi
    public val debug = Debug(this)

    @InternalStreamVideoApi
    public class Debug(val call: Call) {

        public fun doFullReconnection() {
            call.session?.sfuConnectionModule?.sfuSocket?.cancel()
        }

        public fun restartSubscriberIce() {
            call.session?.subscriber?.connection?.restartIce()
        }

        public fun restartPublisherIce() {
            call.session?.publisher?.connection?.restartIce()
        }

        public fun switchSfu() {
            call.scope.launch {
                call.switchSfu()
            }
        }
    }

    companion object {

        internal var testInstanceProvider = TestInstanceProvider()

        internal class TestInstanceProvider {
            var mediaManagerCreator: (() -> MediaManagerImpl)? = null
            var rtcSessionCreator: (() -> RtcSession)? = null
        }
    }
}

public data class CreateCallOptions(
    val memberIds: List<String>? = null,
    val members: List<MemberRequest>? = null,
    val custom: Map<String, Any>? = null,
    val settings: CallSettingsRequest? = null,
    val startsAt: OffsetDateTime? = null,
    val team: String? = null,
) {
    fun memberRequestsFromIds(): List<MemberRequest> {
        val memberRequestList: MutableList<MemberRequest> = mutableListOf<MemberRequest>()
        if (memberIds != null) {
            memberRequestList.addAll(memberIds.map { MemberRequest(userId = it) })
        }
        if (members != null) {
            memberRequestList.addAll(members)
        }
        return memberRequestList
    }
}
