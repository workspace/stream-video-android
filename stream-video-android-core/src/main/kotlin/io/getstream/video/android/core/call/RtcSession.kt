/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
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

package io.getstream.video.android.core.call

import androidx.annotation.VisibleForTesting
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import io.getstream.result.Result.Failure
import io.getstream.result.Result.Success
import io.getstream.result.onSuccessSuspend
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.DeviceStatus
import io.getstream.video.android.core.MediaManagerImpl
import io.getstream.video.android.core.ScreenShareManager
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoImpl
import io.getstream.video.android.core.call.connection.StreamPeerConnection
import io.getstream.video.android.core.call.stats.model.RtcStatsReport
import io.getstream.video.android.core.call.utils.stringify
import io.getstream.video.android.core.dispatchers.DispatcherProvider
import io.getstream.video.android.core.errors.RtcException
import io.getstream.video.android.core.events.ChangePublishQualityEvent
import io.getstream.video.android.core.events.ICETrickleEvent
import io.getstream.video.android.core.events.JoinCallResponseEvent
import io.getstream.video.android.core.events.ParticipantJoinedEvent
import io.getstream.video.android.core.events.ParticipantLeftEvent
import io.getstream.video.android.core.events.SfuDataEvent
import io.getstream.video.android.core.events.SubscriberOfferEvent
import io.getstream.video.android.core.events.TrackPublishedEvent
import io.getstream.video.android.core.events.TrackUnpublishedEvent
import io.getstream.video.android.core.internal.module.ConnectionModule
import io.getstream.video.android.core.internal.module.SfuConnectionModule
import io.getstream.video.android.core.model.AudioTrack
import io.getstream.video.android.core.model.IceCandidate
import io.getstream.video.android.core.model.IceServer
import io.getstream.video.android.core.model.MediaTrack
import io.getstream.video.android.core.model.StreamPeerType
import io.getstream.video.android.core.model.VideoTrack
import io.getstream.video.android.core.model.toPeerType
import io.getstream.video.android.core.socket.SocketState
import io.getstream.video.android.core.utils.SdpSession
import io.getstream.video.android.core.utils.buildAudioConstraints
import io.getstream.video.android.core.utils.buildConnectionConfiguration
import io.getstream.video.android.core.utils.buildMediaConstraints
import io.getstream.video.android.core.utils.buildRemoteIceServers
import io.getstream.video.android.core.utils.mangleSdpUtil
import io.getstream.video.android.core.utils.mapState
import io.getstream.video.android.core.utils.stringify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.IOException
import org.openapitools.client.models.OwnCapability
import org.openapitools.client.models.VideoEvent
import org.webrtc.CameraEnumerationAndroid.CaptureFormat
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.MediaStreamTrack
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.PeerConnectionState
import org.webrtc.RTCStatsReport
import org.webrtc.RtpParameters.Encoding
import org.webrtc.RtpTransceiver
import org.webrtc.SessionDescription
import retrofit2.HttpException
import stream.video.sfu.event.Migration
import stream.video.sfu.models.ICETrickle
import stream.video.sfu.models.Participant
import stream.video.sfu.models.PeerType
import stream.video.sfu.models.TrackInfo
import stream.video.sfu.models.TrackType
import stream.video.sfu.models.VideoDimension
import stream.video.sfu.models.VideoLayer
import stream.video.sfu.models.VideoQuality
import stream.video.sfu.signal.ICERestartRequest
import stream.video.sfu.signal.ICERestartResponse
import stream.video.sfu.signal.ICETrickleResponse
import stream.video.sfu.signal.SendAnswerRequest
import stream.video.sfu.signal.SendAnswerResponse
import stream.video.sfu.signal.SetPublisherRequest
import stream.video.sfu.signal.SetPublisherResponse
import stream.video.sfu.signal.TrackMuteState
import stream.video.sfu.signal.TrackSubscriptionDetails
import stream.video.sfu.signal.UpdateMuteStatesRequest
import stream.video.sfu.signal.UpdateMuteStatesResponse
import stream.video.sfu.signal.UpdateSubscriptionsRequest
import stream.video.sfu.signal.UpdateSubscriptionsResponse
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * Keeps track of which track is being rendered at what resolution.
 * Also stores if the track is visible or not
 */
data class TrackDimensions(
    var dimensions: VideoDimension,
    var visible: Boolean = false,
)

/**
 * The RtcSession sets up 2 peer connection
 * - The publisher peer connection
 * - The subscriber peer connection
 *
 * It handles everything webrtc related.
 * State is handled by the call state class
 *
 * @see CallState
 *
 * Audio/video management is done by the MediaManager
 *
 * @see MediaManagerImpl
 *
 * This is how the offer/answer cycle works
 *
 * * sessionId is created locally as a random UUID
 * * create the peer connections
 * * capture audio and video (if we're not doing so already, in many apps it should already be on for the preview screen)
 * * execute the join request
 * * add the audio/video tracks which triggers onNegotiationNeeded
 * * onNegotiationNeeded(which calls SetPublisherRequest)
 * * JoinCallResponseEvent returns info on the call's state
 *
 * Dynascale automatically negotiates resolutions across clients
 *
 * * We send what resolutions we want using UpdateSubscriptionsRequest.
 * * It should be triggered as we paginate through participants
 * * Or when the UI layout changes
 * * The SFU tells us what resolution to publish using the ChangePublishQualityEvent event
 *
 */
public class RtcSession internal constructor(
    client: StreamVideo,
    private val connectionModule: ConnectionModule,
    private val call: Call,
    internal var sfuUrl: String,
    internal var sfuToken: String,
    internal var remoteIceServers: List<IceServer>,
    internal var onMigrationCompleted: () -> Unit,
) {

    internal val trackIdToParticipant: MutableStateFlow<Map<String, String>> =
        MutableStateFlow(emptyMap())
    private var syncSubscriberAnswer: Job? = null
    private var syncSubscriberCandidates: Job? = null
    private var syncPublisherJob: Job? = null
    private var subscriptionSyncJob: Job? = null
    private var muteStateSyncJob: Job? = null
    private var sfuSocketStateJob: Job? = null
    private var subscriberListenJob: Job? = null

    private var videoTransceiverInitialized: Boolean = false
    private var audioTransceiverInitialized: Boolean = false
    private var screenshareTransceiverInitialized: Boolean = false
    private var errorJob: Job? = null
    private var eventJob: Job? = null
    internal val socket
        get() = sfuConnectionModule.sfuSocket

    private val logger by taggedLogger("Call:RtcSession")
    private val dynascaleLogger by taggedLogger("Call:RtcSession:Dynascale")
    private val clientImpl = client as StreamVideoImpl

    internal val lastVideoStreamAdded = MutableStateFlow<MediaStream?>(null)

    internal val _peerConnectionStates =
        MutableStateFlow<Pair<PeerConnection.PeerConnectionState?, PeerConnection.PeerConnectionState?>?>(
            null,
        )

    internal val sessionId = clientImpl.sessionId

    val trackDimensions =
        MutableStateFlow<Map<String, Map<TrackType, TrackDimensions>>>(
            emptyMap(),
        )
    val trackDimensionsDebounced = trackDimensions.debounce(100)

    // run all calls on a supervisor job so we can easily cancel them
    private val supervisorJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(clientImpl.scope.coroutineContext + supervisorJob)

    /**
     * We can't publish tracks till we've received the join event response
     */
    private val joinEventResponse: MutableStateFlow<JoinCallResponseEvent?> = MutableStateFlow(null)

    // participants by session id -> participant state
    private val trackPrefixToSessionIdMap =
        call.state.participants.mapState { it.associate { it.trackLookupPrefix to it.sessionId } }

    // We need to update tracks for all participants
    // It's cleaner to store here and have the participant state reference to it
    var tracks: MutableMap<String, MutableMap<TrackType, MediaTrack>> = mutableMapOf()

    private fun getTrack(sessionId: String, type: TrackType): MediaTrack? {
        if (!tracks.containsKey(sessionId)) {
            tracks[sessionId] = mutableMapOf()
        }
        return tracks[sessionId]?.get(type)
    }

    private fun setTrack(sessionId: String, type: TrackType, track: MediaTrack) {
        if (!tracks.containsKey(sessionId)) {
            tracks[sessionId] = mutableMapOf()
        }
        tracks[sessionId]?.set(type, track)

        when (type) {
            TrackType.TRACK_TYPE_VIDEO -> {
                call.state.getParticipantBySessionId(sessionId)?._videoTrack?.value =
                    track.asVideoTrack()
            }

            TrackType.TRACK_TYPE_AUDIO -> {
                call.state.getParticipantBySessionId(sessionId)?._audioTrack?.value =
                    track.asAudioTrack()
            }

            TrackType.TRACK_TYPE_SCREEN_SHARE, TrackType.TRACK_TYPE_SCREEN_SHARE_AUDIO -> {
                call.state.getParticipantBySessionId(sessionId)?._screenSharingTrack?.value =
                    track.asVideoTrack()
            }

            TrackType.TRACK_TYPE_UNSPECIFIED -> {
                logger.w { "Unspecified track type" }
            }
        }
    }

    private fun getLocalTrack(type: TrackType): MediaTrack? {
        return getTrack(sessionId, type)
    }

    private fun setLocalTrack(type: TrackType, track: MediaTrack) {
        return setTrack(sessionId, type, track)
    }

    /**
     * Connection and WebRTC.
     */

    private var iceServers = buildRemoteIceServers(remoteIceServers)

    private val connectionConfiguration: PeerConnection.RTCConfiguration
        get() = buildConnectionConfiguration(iceServers)

    /** subscriber peer connection is used for subs */
    public var subscriber: StreamPeerConnection? = null

    /** publisher for publishing, using 2 peer connections prevents race conditions in the offer/answer cycle */
    internal var publisher: StreamPeerConnection? = null

    private val mediaConstraints: MediaConstraints by lazy {
        buildMediaConstraints()
    }

    private val audioConstraints: MediaConstraints by lazy {
        buildAudioConstraints()
    }

    internal lateinit var sfuConnectionModule: SfuConnectionModule

    /**
     * Used during a SFU migration as a temporary new SFU connection. Is null before and after
     * the migration is finished.
     */
    private var sfuConnectionMigrationModule: SfuConnectionModule? = null

    private val _sfuSocketState = MutableStateFlow<SocketState>(SocketState.NotConnected)
    val sfuSocketState = _sfuSocketState.asStateFlow()

    private val sfuFastReconnectListener: suspend () -> Unit = {
        // SFU socket has done a fast-reconnect. We need to an ICE restart immediately and not wait
        // until the health check runs
        call.monitor.stopTimer()
        call.monitor.reconnect(forceRestart = true)
    }

    init {
        if (!StreamVideo.isInstalled) {
            throw IllegalArgumentException(
                "SDK hasn't been initialised yet - can't start a RtcSession",
            )
        }

        // step 1 setup the peer connections
        subscriber = createSubscriber()

        listenToSubscriberConnection()

        val session = this
        val getSdp = suspend {
            session.getSubscriberSdp().description
        }
        val sfuConnectionModule =
            connectionModule.createSFUConnectionModule(
                sfuUrl,
                sessionId,
                sfuToken,
                getSdp,
                sfuFastReconnectListener,
            )
        setSfuConnectionModule(sfuConnectionModule)
        listenToSocketEventsAndErrors()

        coroutineScope.launch {
            // call update participant subscriptions debounced
            trackDimensionsDebounced.collect {
                setVideoSubscriptions()
            }
        }

        clientImpl.peerConnectionFactory.setAudioSampleCallback { it ->
            call.processAudioSample(it)
        }

        clientImpl.peerConnectionFactory.setAudioRecordDataCallback { audioFormat, channelCount, sampleRate, sampleData ->
            call.audioFilter?.filter(
                audioFormat = audioFormat,
                channelCount = channelCount,
                sampleRate = sampleRate,
                sampleData = sampleData,
            )
        }
    }

    private fun listenToSocketEventsAndErrors() {
        // cancel any old socket monitoring if needed
        eventJob?.cancel()
        errorJob?.cancel()

        // listen to socket events and errors
        eventJob = coroutineScope.launch {
            sfuConnectionModule.sfuSocket.events.collect {
                clientImpl.fireEvent(it, call.cid)
            }
        }
        errorJob = coroutineScope.launch {
            sfuConnectionModule.sfuSocket.errors.collect {
                logger.e(it) { "permanent failure on socket connection" }
                if (clientImpl.developmentMode) {
                    throw it
                }
            }
        }
    }

    private fun updatePeerState() {
        _peerConnectionStates.value = Pair(
            subscriber?.state?.value,
            publisher?.state?.value,
        )
    }

    /**
     * @param forceRestart - set to true if you want to force restart both ICE connections
     * regardless of their current connection status (even if they are CONNECTED)
     */
    suspend fun reconnect(forceRestart: Boolean) {
        // ice restart
        val subscriberAsync = coroutineScope.async {
            subscriber?.let {
                if (!it.isHealthy()) {
                    logger.i { "ice restarting subscriber peer connection" }
                    requestSubscriberIceRestart()
                }
            }
        }

        val publisherAsync = coroutineScope.async {
            publisher?.let {
                if (!it.isHealthy() || forceRestart) {
                    logger.i { "ice restarting publisher peer connection (force restart = $forceRestart)" }
                    it.connection.restartIce()
                }
            }
        }

        awaitAll(subscriberAsync, publisherAsync)
    }

    suspend fun connect() {
        val timer = clientImpl.debugInfo.trackTime("sfu ws")
        sfuConnectionModule.sfuSocket.connect()
        timer.finish()

        // ensure that the join event has been handled before starting RTC
        joinEventResponse.first { it != null }
        connectRtc()
    }

    private fun initializeVideoTransceiver() {
        if (!videoTransceiverInitialized) {
            publisher?.let {
                it.addVideoTransceiver(
                    call.mediaManager.videoTrack,
                    listOf(buildTrackId(TrackType.TRACK_TYPE_VIDEO)),
                    isScreenShare = false,
                )
                videoTransceiverInitialized = true
            }
        }
    }

    private fun setSfuConnectionModule(sfuConnectionModule: SfuConnectionModule) {
        // This is used to switch from a current SFU connection to a new migrated SFU connection
        this@RtcSession.sfuConnectionModule = sfuConnectionModule

        // Stop listening to connection state on the existing SFU connection
        sfuSocketStateJob?.cancel()
        sfuSocketStateJob = null

        // Start listening to connection state on new SFU connection
        sfuSocketStateJob = coroutineScope.launch {
            sfuConnectionModule.sfuSocket.connectionState.collect { sfuSocketState ->
                _sfuSocketState.value = sfuSocketState

                // make sure we stop handling ICE candidates when a new SFU socket
                // connection is being established. We need to wait until a SubscriberOffer
                // is received again and then we start listening to the ICE candidate queue
                if (sfuSocketState == SocketState.Connecting ||
                    sfuSocketState is SocketState.DisconnectedTemporarily ||
                    sfuSocketState is SocketState.DisconnectedByRequest
                ) {
                    syncSubscriberCandidates?.cancel()
                    syncSubscriberCandidates?.cancel()
                }
            }
        }
    }

    private fun initializeScreenshareTransceiver() {
        if (!screenshareTransceiverInitialized) {
            publisher?.let {
                it.addVideoTransceiver(
                    call.mediaManager.screenShareTrack,
                    listOf(buildTrackId(TrackType.TRACK_TYPE_SCREEN_SHARE)),
                    isScreenShare = true,
                )
                screenshareTransceiverInitialized = true
            }
        }
    }

    private fun initialiseAudioTransceiver() {
        if (!audioTransceiverInitialized) {
            publisher?.let {
                it.addAudioTransceiver(
                    call.mediaManager.audioTrack,
                    listOf(buildTrackId(TrackType.TRACK_TYPE_AUDIO)),
                )
                audioTransceiverInitialized = true
            }
        }
    }

    private suspend fun listenToMediaChanges() {
        coroutineScope.launch {
            // update the tracks when the camera or microphone status changes
            call.mediaManager.camera.status.collectLatest {
                // set the mute /unumute status
                setMuteState(isEnabled = it == DeviceStatus.Enabled, TrackType.TRACK_TYPE_VIDEO)

                if (it == DeviceStatus.Enabled) {
                    initializeVideoTransceiver()
                }
            }
        }
        coroutineScope.launch {
            call.mediaManager.microphone.status.collectLatest {
                // set the mute /unumute status
                setMuteState(isEnabled = it == DeviceStatus.Enabled, TrackType.TRACK_TYPE_AUDIO)

                if (it == DeviceStatus.Enabled) {
                    initialiseAudioTransceiver()
                }
            }
        }

        coroutineScope.launch {
            call.mediaManager.screenShare.status.collectLatest {
                // set the mute /unumute status
                setMuteState(
                    isEnabled = it == DeviceStatus.Enabled,
                    TrackType.TRACK_TYPE_SCREEN_SHARE,
                )

                if (it == DeviceStatus.Enabled) {
                    initializeScreenshareTransceiver()
                }
            }
        }
    }

    /**
     * A single media stream contains multiple tracks. We receive it from the subcriber peer connection
     *
     * Loop over the audio and video tracks
     * Update the local tracks
     *
     * Audio is available from the start.
     * Video only becomes available after we update the subscription
     */
    private fun addStream(mediaStream: MediaStream) {
        val (trackPrefix, trackTypeString) = mediaStream.id.split(':')
        val sessionId = trackPrefixToSessionIdMap.value[trackPrefix]

        if (sessionId == null || trackPrefixToSessionIdMap.value[trackPrefix].isNullOrEmpty()) {
            logger.d { "[addStream] skipping unrecognized trackPrefix $trackPrefix $mediaStream.id" }
            return
        }

        val trackTypeMap = mapOf(
            "TRACK_TYPE_UNSPECIFIED" to TrackType.TRACK_TYPE_UNSPECIFIED,
            "TRACK_TYPE_AUDIO" to TrackType.TRACK_TYPE_AUDIO,
            "TRACK_TYPE_VIDEO" to TrackType.TRACK_TYPE_VIDEO,
            "TRACK_TYPE_SCREEN_SHARE" to TrackType.TRACK_TYPE_SCREEN_SHARE,
            "TRACK_TYPE_SCREEN_SHARE_AUDIO" to TrackType.TRACK_TYPE_SCREEN_SHARE_AUDIO,
        )
        val trackType =
            trackTypeMap[trackTypeString] ?: TrackType.fromValue(trackTypeString.toInt())
                ?: throw IllegalStateException("trackType not recognized: $trackTypeString")

        logger.i { "[] #sfu; mediaStream: $mediaStream" }
        mediaStream.audioTracks.forEach { track ->
            logger.v { "[addStream] #sfu; audioTrack: ${track.stringify()}" }
            track.setEnabled(true)
            val audioTrack = AudioTrack(
                streamId = mediaStream.id,
                audio = track,
            )
            val current = trackIdToParticipant.value.toMutableMap()
            current[track.id()] = sessionId
            trackIdToParticipant.value = current

            setTrack(sessionId, trackType, audioTrack)
        }

        mediaStream.videoTracks.forEach { track ->
            track.setEnabled(true)
            val videoTrack = VideoTrack(
                streamId = mediaStream.id,
                video = track,
            )
            val current = trackIdToParticipant.value.toMutableMap()
            current[track.id()] = sessionId
            trackIdToParticipant.value = current

            setTrack(sessionId, trackType, videoTrack)
        }
        if (sessionId != this.sessionId && mediaStream.videoTracks.isNotEmpty()) {
            lastVideoStreamAdded.value = mediaStream
        }
    }

    private suspend fun connectRtc() {
        val settings = call.state.settings.value
        val timer = clientImpl.debugInfo.trackTime("connectRtc")

        // turn of the speaker if needed
        if (settings?.audio?.speakerDefaultOn == false) {
            call.speaker.setVolume(0)
        }

        // if we are allowed to publish, create a peer connection for it
        val canPublish =
            call.state.ownCapabilities.value.any {
                it == OwnCapability.SendAudio || it == OwnCapability.SendVideo
            }

        if (canPublish) {
            publisher = createPublisher()
            timer.split("createPublisher")
        } else {
            // enable the publisher if you receive the send audio or send video capability
            coroutineScope.launch {
                call.state.ownCapabilities.collect {
                    if (it.any { it == OwnCapability.SendAudio || it == OwnCapability.SendVideo }) {
                        publisher = createPublisher()
                        timer.split("createPublisher")
                    }
                }
            }
        }

        // update the peer state
        coroutineScope.launch {
            // call update participant subscriptions debounced
            publisher?.let {
                it.state.collect {
                    updatePeerState()
                }
            }
        }

        if (canPublish) {
            if (publisher == null) {
                throw IllegalStateException(
                    "Cant send audio and video since publisher hasn't been setup to connect",
                )
            }
            publisher?.let { publisher ->
                // step 2 ensure all tracks are setup correctly
                // start capturing the video

                timer.split("media enabled")
                // step 4 add the audio track to the publisher
                setLocalTrack(
                    TrackType.TRACK_TYPE_AUDIO,
                    AudioTrack(
                        streamId = buildTrackId(TrackType.TRACK_TYPE_AUDIO),
                        audio = call.mediaManager.audioTrack,
                    ),
                )

                // step 5 create the video track
                setLocalTrack(
                    TrackType.TRACK_TYPE_VIDEO,
                    VideoTrack(
                        streamId = buildTrackId(TrackType.TRACK_TYPE_VIDEO),
                        video = call.mediaManager.videoTrack,
                    ),
                )

                // render it on the surface. but we need to start this before forwarding it to the publisher
                logger.v { "[createUserTracks] #sfu; videoTrack: ${call.mediaManager.videoTrack.stringify()}" }
                if (call.mediaManager.camera.status.value == DeviceStatus.Enabled) {
                    initializeVideoTransceiver()
                }
                if (call.mediaManager.microphone.status.value == DeviceStatus.Enabled) {
                    initialiseAudioTransceiver()
                }
                if (call.mediaManager.screenShare.status.value == DeviceStatus.Enabled) {
                    initializeScreenshareTransceiver()
                }
            }
        }

        // step 6 - onNegotiationNeeded will trigger and complete the setup using SetPublisherRequest
        timer.finish()
        listenToMediaChanges()

        // subscribe to the tracks of other participants
        setVideoSubscriptions(true)
        return
    }

    fun setScreenShareTrack() {
        setLocalTrack(
            TrackType.TRACK_TYPE_SCREEN_SHARE,
            VideoTrack(
                streamId = buildTrackId(TrackType.TRACK_TYPE_SCREEN_SHARE),
                video = call.mediaManager.screenShareTrack,
            ),
        )
    }

    /**
     * Responds to TrackPublishedEvent event
     * @see TrackPublishedEvent
     * @see TrackUnpublishedEvent
     *
     * It gets the participant and updates the tracks
     *
     * Track look is done by sessionId & type
     */
    internal fun updatePublishState(
        userId: String,
        sessionId: String,
        trackType: TrackType,
        videoEnabled: Boolean,
        audioEnabled: Boolean,
    ) {
        logger.d {
            "[updateMuteState] #sfu; userId: $userId, sessionId: $sessionId, videoEnabled: $videoEnabled, audioEnabled: $audioEnabled"
        }
        val track = getTrack(sessionId, trackType)
        track?.enableVideo(videoEnabled)
        track?.enableAudio(audioEnabled)
    }

    fun cleanup() {
        logger.i { "[cleanup] #sfu; no args" }
        supervisorJob.cancel()

        // disconnect the socket and clean it up
        sfuConnectionModule.sfuSocket.cleanup()

        sfuConnectionMigrationModule?.sfuSocket?.cleanup()
        sfuConnectionMigrationModule = null

        // cleanup the publisher and subcriber peer connections
        subscriber?.connection?.close()
        publisher?.connection?.close()

        subscriber = null
        publisher = null

        // cleanup all non-local tracks
        tracks.filter { it.key != sessionId }.values.map { it.values }.flatten()
            .forEach { wrapper ->
                try {
                    wrapper.asAudioTrack()?.audio?.dispose()
                    wrapper.asVideoTrack()?.video?.dispose()
                } catch (e: Exception) {
                    logger.w { "Error disposing track: ${e.message}" }
                }
            }
        tracks.clear()

        trackDimensions.value = emptyMap()
    }

    internal val muteState = MutableStateFlow(
        mapOf(
            TrackType.TRACK_TYPE_AUDIO to false,
            TrackType.TRACK_TYPE_VIDEO to false,
            TrackType.TRACK_TYPE_SCREEN_SHARE to false,
        ),
    )

    /**
     * Informs the SFU that you're publishing a given track (publishing vs muted)
     * - when switching SFU we should repeat this info
     * - http calls failing here breaks the call. it should retry as long as the
     * -- error isn't permanent, SFU didn't change, the mute/publish state didn't change
     * -- we cap at 30 retries to prevent endless loops
     */
    private fun setMuteState(isEnabled: Boolean, trackType: TrackType) {
        logger.d { "[setPublishState] #sfu; $trackType isEnabled: $isEnabled" }

        // update the local copy
        val copy = muteState.value.toMutableMap()
        copy[trackType] = isEnabled
        val new = copy.toMap()
        muteState.value = new

        val currentSfu = sfuUrl
        // prevent running multiple of these at the same time
        // if there's already a job active. cancel it
        muteStateSyncJob?.cancel()
        // start a new job
        // this code is a bit more complicated due to the retry behaviour
        muteStateSyncJob = coroutineScope.launch {
            flow {
                val request = UpdateMuteStatesRequest(
                    session_id = sessionId,
                    mute_states = copy.map {
                        TrackMuteState(track_type = it.key, muted = !it.value)
                    },
                )
                val result = updateMuteState(request)
                result.onSuccessSuspend { emit(result.getOrThrow()) }
            }.flowOn(DispatcherProvider.IO).retryWhen { cause, attempt ->
                val sameValue = new == muteState.value
                val sameSfu = currentSfu == sfuUrl
                val isPermanent = isPermanentError(cause)
                val willRetry = !isPermanent && sameValue && sameSfu && attempt < 30
                val delayInMs = if (attempt <= 1) 100L else if (attempt <= 3) 300L else 2500L
                logger.w {
                    "updating mute state failed with error $cause, retry attempt: $attempt. will retry $willRetry in $delayInMs ms"
                }
                delay(delayInMs)
                willRetry
            }.collect()
        }
    }

    private fun isPermanentError(cause: Throwable): Boolean {
        return false
    }

    @VisibleForTesting
    public fun createSubscriber(): StreamPeerConnection {
        logger.i { "[createSubscriber] #sfu" }
        val peerConnection = clientImpl.peerConnectionFactory.makePeerConnection(
            coroutineScope = coroutineScope,
            configuration = connectionConfiguration,
            type = StreamPeerType.SUBSCRIBER,
            mediaConstraints = mediaConstraints,
            onStreamAdded = { addStream(it) }, // addTrack
            onIceCandidateRequest = ::sendIceCandidate,
        )
        peerConnection.connection.addTransceiver(
            MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
            RtpTransceiver.RtpTransceiverInit(
                RtpTransceiver.RtpTransceiverDirection.RECV_ONLY,
            ),
        )

        peerConnection.connection.addTransceiver(
            MediaStreamTrack.MediaType.MEDIA_TYPE_AUDIO,
            RtpTransceiver.RtpTransceiverInit(
                RtpTransceiver.RtpTransceiverDirection.RECV_ONLY,
            ),
        )
        return peerConnection
    }

    private suspend fun getSubscriberSdp(): SessionDescription {
        subscriber?.let { it ->
            val result = it.createOffer()

            return if (result is Success) {
                mangleSdp(result.value)
            } else {
                throw Error("Couldn't create a generic SDP, create offer failed")
            }
        } ?: throw Error("Couldn't create a generic SDP, subscriber isn't setup")
    }

    private fun mangleSdp(sdp: SessionDescription): SessionDescription {
        val settings = call.state.settings.value
        val red = settings?.audio?.redundantCodingEnabled ?: true
        val opus = settings?.audio?.opusDtxEnabled ?: true

        return mangleSdpUtil(sdp, red, opus)
    }

    @VisibleForTesting
    fun createPublisher(): StreamPeerConnection {
        val publisher = clientImpl.peerConnectionFactory.makePeerConnection(
            coroutineScope = coroutineScope,
            configuration = connectionConfiguration,
            type = StreamPeerType.PUBLISHER,
            mediaConstraints = MediaConstraints(),
            onNegotiationNeeded = ::onNegotiationNeeded,
            onIceCandidateRequest = ::sendIceCandidate,
            maxPublishingBitrate = call.state.settings.value?.video?.targetResolution?.bitrate
                ?: 1_200_000,
        )
        logger.i { "[createPublisher] #sfu; publisher: $publisher" }
        return publisher
    }

    private fun buildTrackId(trackTypeVideo: TrackType): String {
        // track prefix is only available after the join response
        val trackType = trackTypeVideo.value
        val trackPrefix = call.state.me.value?.trackLookupPrefix
        val old = "$trackPrefix:$trackType:${(Math.random() * 100).toInt()}"
        return old // UUID.randomUUID().toString()
    }

    /**
     * Change the quality of video we upload when the ChangePublishQualityEvent event is received
     * This is used for dynsacle
     */
    internal fun updatePublishQuality(event: ChangePublishQualityEvent) = synchronized(this) {
        val sender = publisher?.connection?.transceivers?.firstOrNull {
            it.mediaType == MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO
        }?.sender

        if (sender == null) {
            dynascaleLogger.w {
                "Request to change publishing quality not fulfilled due to missing transceivers or sender."
            }
            return@synchronized
        }

        val enabledRids = event.changePublishQuality.video_senders.firstOrNull()?.layers?.associate {
            it.name to it.active
        }
        dynascaleLogger.i { "enabled rids: $enabledRids}" }
        val params = sender.parameters
        val updatedEncodings: MutableList<Encoding> = mutableListOf()
        var changed = false
        for (encoding in params.encodings) {
            val shouldEnable = enabledRids?.get(encoding.rid) ?: false
            if (shouldEnable && encoding.active) {
                updatedEncodings.add(encoding)
            } else if (!shouldEnable && !encoding.active) {
                updatedEncodings.add(encoding)
            } else {
                changed = true
                encoding.active = shouldEnable
                updatedEncodings.add(encoding)
            }
        }
        if (changed) {
            dynascaleLogger.i { "Updated publish quality with encodings $updatedEncodings" }
            params.encodings.clear()
            params.encodings.addAll(updatedEncodings)
            sender.parameters = params
        }
    }

    private val defaultVideoDimension = VideoDimension(1080, 2340)

    /**
     * This is called when you are look at a different set of participants
     * or at a different size
     *
     * It tells the SFU that we want to receive person a's video at 1080p, and person b at 360p
     *
     * Since the viewmodel knows what's actually displayed
     */
    internal fun defaultTracks(): List<TrackSubscriptionDetails> {
        val sortedParticipants = call.state.participants.value
        val otherParticipants = sortedParticipants.filter { it.sessionId != sessionId }.take(5)
        val tracks = mutableListOf<TrackSubscriptionDetails>()
        otherParticipants.forEach { participant ->
            if (participant.videoEnabled.value) {
                val track = TrackSubscriptionDetails(
                    user_id = participant.userId.value,
                    track_type = TrackType.TRACK_TYPE_VIDEO,
                    dimension = defaultVideoDimension,
                    session_id = participant.sessionId,
                )
                tracks.add(track)
            }
            if (participant.screenSharingEnabled.value) {
                val track = TrackSubscriptionDetails(
                    user_id = participant.userId.value,
                    track_type = TrackType.TRACK_TYPE_SCREEN_SHARE,
                    dimension = defaultVideoDimension,
                    session_id = participant.sessionId,
                )
                tracks.add(track)
            }
        }

        return tracks
    }

    internal fun visibleTracks(): List<TrackSubscriptionDetails> {
        val participants = call.state.remoteParticipants.value
        val trackDisplayResolution = trackDimensions.value

        val tracks = participants.map { participant ->
            val trackDisplay = trackDisplayResolution[participant.sessionId] ?: emptyMap()

            trackDisplay.entries.filter { it.value.visible }.map { display ->
                dynascaleLogger.i {
                    "[visibleTracks] $sessionId subscribing ${participant.sessionId} to : ${display.key}"
                }
                TrackSubscriptionDetails(
                    user_id = participant.userId.value,
                    track_type = display.key,
                    dimension = display.value.dimensions,
                    session_id = participant.sessionId,
                )
            }
        }.flatten()
        return tracks
    }

    internal val subscriptions: MutableStateFlow<List<TrackSubscriptionDetails>> = MutableStateFlow(
        emptyList(),
    )

    /**
     * Tells the SFU which video tracks we want to subscribe to
     * - it sends the resolutions we're displaying the video at so the SFU can decide which track to send
     * - when switching SFU we should repeat this info
     * - http calls failing here breaks the call. (since you won't receive the video)
     * - we should retry continously until it works and after it continues to fail, raise an error that shuts down the call
     * - we retry when:
     * -- error isn't permanent, SFU didn't change, the mute/publish state didn't change
     * -- we cap at 30 retries to prevent endless loops
     */
    private fun setVideoSubscriptions(useDefaults: Boolean = false) {
        // default is to subscribe to the top 5 sorted participants
        var tracks = if (useDefaults) {
            defaultTracks()
        } else {
            // if we're not using the default, sub to visible tracks
            visibleTracks()
        }

        // TODO:
        // This is a hotfix to help with performance. Most devices struggle even with H resolution
        // if there are more than 2 remote participants and especially if there is a H264 participant.
        // We just report a very small window so force SFU to deliver us Q resolution. This will
        // be later less visible to the user once we make the participant grid smaller
        if (tracks.size > 2) {
            tracks = tracks.map {
                it.copy(dimension = it.dimension?.copy(width = 200, height = 200))
            }
        }

        val new = tracks.toList()
        subscriptions.value = new
        val currentSfu = sfuUrl

        subscriptionSyncJob?.cancel()

        if (new.isNotEmpty()) {
            // start a new job
            // this code is a bit more complicated due to the retry behaviour
            subscriptionSyncJob = coroutineScope.launch {
                flow {
                    val request = UpdateSubscriptionsRequest(
                        session_id = sessionId,
                        tracks = subscriptions.value,
                    )
                    println("request $request")
                    val sessionToDimension = tracks.map { it.session_id to it.dimension }
                    dynascaleLogger.i {
                        "[setVideoSubscriptions] $useDefaults #sfu; $sessionId subscribing to : $sessionToDimension"
                    }
                    val result = updateSubscriptions(request)
                    emit(result.getOrThrow())
                }.flowOn(DispatcherProvider.IO).retryWhen { cause, attempt ->
                    val sameValue = new == subscriptions.value
                    val sameSfu = currentSfu == sfuUrl
                    val isPermanent = isPermanentError(cause)
                    val willRetry = !isPermanent && sameValue && sameSfu && attempt < 30
                    val delayInMs = if (attempt <= 1) 100L else if (attempt <= 3) 300L else 2500L
                    logger.w {
                        "updating subscriptions failed with error $cause, retry attempt: $attempt. will retry $willRetry in $delayInMs ms"
                    }
                    delay(delayInMs)
                    willRetry
                }.collect()
            }
        }
    }

    fun handleEvent(event: VideoEvent) {
        logger.i { "[rtc handleEvent] #sfu; event: $event" }
        if (event is JoinCallResponseEvent) {
            logger.i { "[rtc handleEvent] joinEventResponse.value: $event" }

            joinEventResponse.value = event
        }
        if (event is SfuDataEvent) {
            coroutineScope.launch {
                logger.v { "[onRtcEvent] event: $event" }
                when (event) {
                    is SubscriberOfferEvent -> handleSubscriberOffer(event)
                    // this dynascale event tells the SDK to change the quality of the video it's uploading
                    is ChangePublishQualityEvent -> updatePublishQuality(event)

                    is TrackPublishedEvent -> {
                        updatePublishState(
                            userId = event.userId,
                            sessionId = event.sessionId,
                            trackType = event.trackType,
                            videoEnabled = true,
                            audioEnabled = true,
                        )
                    }

                    is TrackUnpublishedEvent -> {
                        updatePublishState(
                            userId = event.userId,
                            sessionId = event.sessionId,
                            trackType = event.trackType,
                            videoEnabled = false,
                            audioEnabled = false,
                        )
                    }

                    is ParticipantJoinedEvent -> {
                        // the UI layer will automatically trigger updateParticipantsSubscriptions
                    }

                    is ParticipantLeftEvent -> {
                        removeParticipantTracks(event.participant)
                        removeParticipantTrackDimensions(event.participant)
                    }

                    else -> {
                        logger.d { "[onRtcEvent] skipped event: $event" }
                    }
                }
            }
        }
    }

    private fun removeParticipantTracks(participant: Participant) {
        tracks.remove(participant.session_id).also {
            if (it == null) {
                logger.e {
                    "[handleEvent] Failed to remove track on ParticipantLeft " +
                        "- track ID: ${participant.session_id}). Tracks: $tracks"
                }
            }
        }
    }

    private fun removeParticipantTrackDimensions(participant: Participant) {
        val newTrackDimensions = trackDimensions.value.toMutableMap()
        newTrackDimensions.remove(participant.session_id).also {
            if (it == null) {
                logger.e {
                    "[handleEvent] Failed to remove track dimension on ParticipantLeft " +
                        "- track ID: ${participant.session_id}). TrackDimensions: $newTrackDimensions"
                }
            }
        }
        trackDimensions.value = newTrackDimensions
    }

    /**
     Section, basic webrtc calls
     */

    /**
     * Whenever onIceCandidateRequest is called we send the ice candidate
     */
    private fun sendIceCandidate(candidate: IceCandidate, peerType: StreamPeerType) {
        coroutineScope.launch {
            flow {
                logger.d { "[sendIceCandidate] #sfu; #${peerType.stringify()}; candidate: $candidate" }
                val iceTrickle = ICETrickle(
                    peer_type = peerType.toPeerType(),
                    ice_candidate = Json.encodeToString(candidate),
                    session_id = sessionId,
                )
                logger.v { "[sendIceCandidate] #sfu; #${peerType.stringify()}; iceTrickle: $iceTrickle" }
                val result = sendIceCandidate(iceTrickle)
                logger.v { "[sendIceCandidate] #sfu; #${peerType.stringify()}; completed: $result" }
                emit(result.getOrThrow())
            }.retry(3).catch { logger.w { "sending ice candidate failed" } }.collect()
        }
    }

    @VisibleForTesting
    /**
     * Triggered whenever we receive new ice candidate from the SFU
     */
    suspend fun handleIceTrickle(event: ICETrickleEvent) {
        logger.d {
            "[handleIceTrickle] #sfu; #${event.peerType.stringify()}; candidate: ${event.candidate}"
        }
        val iceCandidate: IceCandidate = Json.decodeFromString(event.candidate)
        val result = if (event.peerType == PeerType.PEER_TYPE_PUBLISHER_UNSPECIFIED) {
            publisher?.addIceCandidate(iceCandidate)
        } else {
            subscriber?.addIceCandidate(iceCandidate)
        }
        logger.v { "[handleTrickle] #sfu; #${event.peerType.stringify()}; result: $result" }
    }

    internal val subscriberSdpAnswer = MutableStateFlow<SessionDescription?>(null)

    @VisibleForTesting
    /**
     * This is called when the SFU sends us an offer
     * - Sets the remote description
     * - Creates an answer
     * - Sets the local description
     * - Sends the answer back to the SFU
     */
    suspend fun handleSubscriberOffer(offerEvent: SubscriberOfferEvent) {
        logger.d { "[handleSubscriberOffer] #sfu; #subscriber; event: $offerEvent" }
        val subscriber = subscriber ?: return

        syncSubscriberCandidates?.cancel()

        // step 1 - receive the offer and set it to the remote
        val offerDescription = SessionDescription(
            SessionDescription.Type.OFFER,
            offerEvent.sdp,
        )
        subscriber.setRemoteDescription(offerDescription)

        // step 2 - create the answer
        val answerResult = subscriber.createAnswer()
        if (answerResult !is Success) {
            logger.w {
                "[handleSubscriberOffer] #sfu; #subscriber; rejected (createAnswer failed): $answerResult"
            }
            return
        }
        val answerSdp = mangleSdp(answerResult.value)
        logger.v { "[handleSubscriberOffer] #sfu; #subscriber; answerSdp: ${answerSdp.description}" }

        // step 3 - set local description
        val setAnswerResult = subscriber.setLocalDescription(answerSdp)
        if (setAnswerResult !is Success) {
            logger.w {
                "[handleSubscriberOffer] #sfu; #subscriber; rejected (setAnswer failed): $setAnswerResult"
            }
            return
        }
        subscriberSdpAnswer.value = answerSdp
        // TODO: we could handle SFU changes by having a coroutine job per SFU and just cancel it when it switches
        // TODO: retry behaviour could be cleaned up into 3 different extension functions for better readability
        // see: https://www.notion.so/stream-wiki/Video-Development-Guide-fef3ece1c643455889f2c0fdba74a89d
        val currentSfu = sfuUrl

        // prevent running multiple of these at the same time
        // if there's already a job active. cancel it
        syncSubscriberAnswer?.cancel()
        // start a new job
        // this code is a bit more complicated due to the retry behaviour
        syncSubscriberAnswer = coroutineScope.launch {
            flow {
                // step 4 - send the answer
                logger.v { "[handleSubscriberOffer] #sfu; #subscriber; setAnswerResult: $setAnswerResult" }
                val sendAnswerRequest = SendAnswerRequest(
                    PeerType.PEER_TYPE_SUBSCRIBER, answerSdp.description, sessionId,
                )
                val sendAnswerResult = sendAnswer(sendAnswerRequest)
                logger.v { "[handleSubscriberOffer] #sfu; #subscriber; sendAnswerResult: $sendAnswerResult" }
                emit(sendAnswerResult.getOrThrow())

                // setRemoteDescription has been called and everything is ready - we can
                // now start handling the ICE subscriber candidates queue
                syncSubscriberCandidates = coroutineScope.launch {
                    sfuConnectionModule.sfuSocket.pendingSubscriberIceCandidates.collect { iceCandidates ->
                        subscriber.addIceCandidate(iceCandidates)
                    }
                }
            }.flowOn(DispatcherProvider.IO).retryWhen { cause, attempt ->
                val sameValue = answerSdp == subscriberSdpAnswer.value
                val sameSfu = currentSfu == sfuUrl
                val isPermanent = isPermanentError(cause)
                val willRetry = !isPermanent && sameValue && sameSfu && attempt <= 3
                val delayInMs = if (attempt <= 1) 10L else if (attempt <= 2) 30L else 100L
                logger.w {
                    "sendAnswer failed $cause, retry attempt: $attempt. will retry $willRetry in $delayInMs ms"
                }
                delay(delayInMs)
                willRetry
            }.catch {
                logger.e { "setPublisher failed after 3 retries, asking the call monitor to do an ice restart" }
                coroutineScope.launch { call.monitor.reconnect(forceRestart = true) }
            }.collect()
        }
    }

    internal val publisherSdpOffer = MutableStateFlow<SessionDescription?>(null)

    /**
     * https://developer.mozilla.org/en-US/docs/Web/API/RTCPeerConnection/negotiationneeded_event
     *
     * Is called whenever a negotiation is needed. Common examples include
     * - Adding a new media stream
     * - Adding an audio Stream
     * - A screenshare track is started
     *
     * Creates a new SDP
     * - And sets it on the localDescription
     * - Enables video simulcast
     * - calls setPublisher
     * - sets setRemoteDescription
     *
     * Retry behaviour is to retry 3 times quickly as long as
     * - the sfu didn't change
     * - the sdp didn't change
     * If that fails ask the call monitor to do an ice restart
     */
    @VisibleForTesting
    fun onNegotiationNeeded(
        peerConnection: StreamPeerConnection,
        peerType: StreamPeerType,
    ) {
        val id = Random.nextInt().absoluteValue
        logger.d { "[negotiate] #$id; #sfu; #${peerType.stringify()}; peerConnection: $peerConnection" }
        coroutineScope.launch {
            // step 1 - create a local offer
            val offerResult = peerConnection.createOffer()
            if (offerResult !is Success) {
                logger.w {
                    "[negotiate] #$id; #sfu; #${peerType.stringify()}; rejected (createOffer failed): $offerResult"
                }
                return@launch
            }
            val mangledSdp = mangleSdp(offerResult.value)

            // step 2 -  set the local description
            val result = peerConnection.setLocalDescription(mangledSdp)
            if (result.isFailure) {
                // the call health monitor will end up restarting the peer connection and recover from this
                logger.w { "[negotiate] #$id; #sfu; #${peerType.stringify()}; setLocalDescription failed: $result" }
                return@launch
            }

            // the Sfu WS needs to be connected before calling SetPublisherRequest
            if (joinEventResponse.value == null) {
                logger.e { "[negotiate] #$id; #sfu; #${peerType.stringify()}; SFU WS isn't connected" }
                return@launch
            }

            // step 3 - create the list of tracks
            val tracks = getPublisherTracks(mangledSdp.description)
            val currentSfu = sfuUrl

            publisherSdpOffer.value = mangledSdp

            // prevent running multiple of these at the same time
            // if there's already a job active. cancel it
            syncPublisherJob?.cancel()
            // start a new job
            // this code is a bit more complicated due to the retry behaviour
            syncPublisherJob = coroutineScope.launch {
                flow {
                    // step 4 - send the tracks and SDP
                    val request = SetPublisherRequest(
                        sdp = mangledSdp.description,
                        session_id = sessionId,
                        tracks = tracks,
                    )

                    val result = setPublisher(request)
                    // step 5 - set the remote description

                    peerConnection.setRemoteDescription(
                        SessionDescription(
                            SessionDescription.Type.ANSWER, result.getOrThrow().sdp,
                        ),
                    )

                    // start listening to ICE candidates
                    launch {
                        sfuConnectionModule.sfuSocket.pendingPublisherIceCandidates.collect { iceCandidates ->
                            publisher?.addIceCandidate(iceCandidates)
                        }
                    }

                    emit(result.getOrThrow())
                }.flowOn(DispatcherProvider.IO).retryWhen { cause, attempt ->
                    val sameValue = mangledSdp == publisherSdpOffer.value
                    val sameSfu = currentSfu == sfuUrl
                    val isPermanent = isPermanentError(cause)
                    val willRetry = !isPermanent && sameValue && sameSfu && attempt <= 3
                    val delayInMs = if (attempt <= 1) 10L else if (attempt <= 2) 30L else 100L
                    logger.w {
                        "onNegotationNeeded setPublisher failed $cause, retry attempt: $attempt. will retry $willRetry in $delayInMs ms"
                    }
                    delay(delayInMs)
                    willRetry
                }.catch {
                    logger.e { "setPublisher failed after 3 retries, asking the call monitor to do an ice restart" }
                    coroutineScope.launch { call.monitor.reconnect(forceRestart = true) }
                }.collect()
            }
        }
    }

    private fun getPublisherTracks(sdp: String): List<TrackInfo> {
        val captureResolution = call.camera.resolution.value
        val screenShareTrack = getLocalTrack(TrackType.TRACK_TYPE_SCREEN_SHARE)

        val transceivers = publisher?.connection?.transceivers?.toList() ?: emptyList()
        val tracks = transceivers.filter {
            it.direction == RtpTransceiver.RtpTransceiverDirection.SEND_ONLY && it.sender?.track() != null
        }.map { transceiver ->
            val track = transceiver.sender.track()!!

            val trackType = convertKindToTrackType(track, screenShareTrack)

            val layers: List<VideoLayer> = if (trackType == TrackType.TRACK_TYPE_VIDEO) {
                checkNotNull(captureResolution) {
                    throw IllegalStateException(
                        "video capture needs to be enabled before adding the local track",
                    )
                }
                createVideoLayers(transceiver, captureResolution)
            } else if (trackType == TrackType.TRACK_TYPE_SCREEN_SHARE) {
                createScreenShareLayers(transceiver)
            } else {
                emptyList()
            }

            TrackInfo(
                track_id = track.id(),
                track_type = trackType,
                layers = layers,
                mid = transceiver.mid ?: extractMid(sdp, track, screenShareTrack, trackType, transceivers),
            )
        }
        return tracks
    }

    private fun convertKindToTrackType(track: MediaStreamTrack, screenShareTrack: MediaTrack?) =
        when (track.kind()) {
            "audio" -> TrackType.TRACK_TYPE_AUDIO
            "screen" -> TrackType.TRACK_TYPE_SCREEN_SHARE
            "video" -> {
                // video tracks and screenshare tracks in webrtc are both video
                // (the "screen" track type doesn't seem to be used).
                if (screenShareTrack?.asVideoTrack()?.video?.id() == track.id()) {
                    TrackType.TRACK_TYPE_SCREEN_SHARE
                } else {
                    TrackType.TRACK_TYPE_VIDEO
                }
            }
            else -> TrackType.TRACK_TYPE_UNSPECIFIED
        }

    private fun extractMid(
        sdp: String?,
        track: MediaStreamTrack,
        screenShareTrack: MediaTrack?,
        trackType: TrackType,
        transceivers: List<RtpTransceiver>,
    ): String {
        if (sdp.isNullOrBlank()) {
            logger.w { "[extractMid] No SDP found. Returning empty mid" }
            return ""
        }

        logger.d {
            "[extractMid] No 'mid' found for track. Trying to find it from the Offer SDP"
        }

        val sdpSession = SdpSession()
        sdpSession.parse(sdp)
        val media = sdpSession.media.find { m ->
            m.mline?.type == track.kind() &&
                // if `msid` is not present, we assume that the track is the first one
                (m.msid?.equals(track.id()) ?: true)
        }

        if (media?.mid == null) {
            logger.d {
                "[extractMid] No mid found in SDP for track type ${track.kind()} and id ${track.id()}. Attempting to find a heuristic mid"
            }

            val heuristicMid = transceivers.firstOrNull {
                convertKindToTrackType(track, screenShareTrack) == trackType
            }
            if (heuristicMid != null) {
                return heuristicMid.mid
            }

            logger.d { "[extractMid] No heuristic mid found. Returning empty mid" }
            return ""
        }

        return media.mid.toString()
    }

    private fun createVideoLayers(transceiver: RtpTransceiver, captureResolution: CaptureFormat): List<VideoLayer> {
        // we tell the Sfu which resolutions we're sending
        return transceiver.sender.parameters.encodings.map {
            val scaleBy = it.scaleResolutionDownBy ?: 1.0
            val width = captureResolution.width.div(scaleBy) ?: 0
            val height = captureResolution.height.div(scaleBy) ?: 0
            val quality = ridToVideoQuality(it.rid)

            // We need to divide by 1000 because the the FramerateRange is multiplied
            // by 1000 (see javadoc).
            val fps = (captureResolution.framerate?.max ?: 0).div(1000)

            VideoLayer(
                rid = it.rid ?: "",
                video_dimension = VideoDimension(
                    width = width.toInt(),
                    height = height.toInt(),
                ),
                bitrate = it.maxBitrateBps ?: 0,
                fps = fps,
                quality = quality,
            )
        }
    }

    private fun createScreenShareLayers(transceiver: RtpTransceiver): List<VideoLayer> {
        return transceiver.sender.parameters.encodings.map {
            // So far we use hardcoded parameters for screen-sharing. This is aligned
            // with iOS.

            VideoLayer(
                rid = "q",
                video_dimension = VideoDimension(
                    width = ScreenShareManager.screenShareResolution.width,
                    height = ScreenShareManager.screenShareResolution.height,
                ),
                bitrate = ScreenShareManager.screenShareBitrate,
                fps = ScreenShareManager.screenShareFps,
                quality = VideoQuality.VIDEO_QUALITY_LOW_UNSPECIFIED,
            )
        }
    }

    private fun ridToVideoQuality(rid: String?) =
        when (rid) {
            "f" -> {
                VideoQuality.VIDEO_QUALITY_HIGH
            }

            "h" -> {
                VideoQuality.VIDEO_QUALITY_MID
            }

            else -> {
                VideoQuality.VIDEO_QUALITY_LOW_UNSPECIFIED
            }
        }

    /**
     * @return [StateFlow] that holds [RtcStatsReport] that the publisher exposes.
     */
    suspend fun getPublisherStats(): RtcStatsReport? {
        return publisher?.getStats()
    }

    /**
     * @return [StateFlow] that holds [RTCStatsReport] that the subscriber exposes.
     */
    suspend fun getSubscriberStats(): RtcStatsReport? {
        return subscriber?.getStats()
    }

    /***
     * Section, API endpoints
     */

    private suspend fun <T : Any> wrapAPICall(apiCall: suspend () -> T): Result<T> {
        return withContext(coroutineScope.coroutineContext) {
            try {
                val result = apiCall()
                Success(result)
            } catch (e: HttpException) {
                // TODO: understand the error conditions here
                parseError(e)
            } catch (e: RtcException) {
                // TODO: understand the error conditions here
                Failure(
                    io.getstream.result.Error.ThrowableError(
                        e.message ?: "RtcException",
                        e,
                    ),
                )
            } catch (e: IOException) {
                // TODO: understand the error conditions here
                Failure(
                    io.getstream.result.Error.ThrowableError(
                        e.message ?: "IOException",
                        e,
                    ),
                )
            }
        }
    }

    private suspend fun parseError(e: Throwable): Failure {
        return Failure(
            io.getstream.result.Error.ThrowableError(
                "CallClientImpl error needs to be handled",
                e,
            ),
        )
    }

    // reply to when we get an offer from the SFU
    private suspend fun sendAnswer(request: SendAnswerRequest): Result<SendAnswerResponse> =
        wrapAPICall {
            val result = sfuConnectionModule.signalService.sendAnswer(request)
            result.error?.let {
                throw RtcException(error = it, message = it.message)
            }
            result
        }

    // send whenever we have a new ice candidate
    private suspend fun sendIceCandidate(request: ICETrickle): Result<ICETrickleResponse> =
        wrapAPICall {
            val result = sfuConnectionModule.signalService.iceTrickle(request)
            result.error?.let {
                throw RtcException(error = it, message = it.message)
            }
            result
        }

    // call after onNegotiation Needed
    private suspend fun setPublisher(request: SetPublisherRequest): Result<SetPublisherResponse> =
        wrapAPICall {
            val result = sfuConnectionModule.signalService.setPublisher(request)
            result.error?.let {
                throw RtcException(error = it, message = it.message)
            }
            result
        }

    // share what size and which participants we're looking at
    private suspend fun updateSubscriptions(
        request: UpdateSubscriptionsRequest,
    ): Result<UpdateSubscriptionsResponse> =
        wrapAPICall {
            val result = sfuConnectionModule.signalService.updateSubscriptions(request)
            result.error?.let {
                throw RtcException(error = it, message = it.message)
            }
            result
        }

    // share what size and which participants we're looking at
    suspend fun requestSubscriberIceRestart(): Result<ICERestartResponse> =
        wrapAPICall {
            val request = ICERestartRequest(
                session_id = sessionId,
                peer_type = PeerType.PEER_TYPE_SUBSCRIBER,
            )
            sfuConnectionModule.signalService.iceRestart(request)
        }

    private suspend fun updateMuteState(request: UpdateMuteStatesRequest): Result<UpdateMuteStatesResponse> =
        wrapAPICall {
            val result = sfuConnectionModule.signalService.updateMuteStates(request)
            result.error?.let {
                throw RtcException(error = it, message = it.message)
            }
            result
        }

    // sets display track visibility
    @Synchronized
    fun updateTrackDimensions(
        sessionId: String,
        trackType: TrackType,
        visible: Boolean,
        dimensions: VideoDimension = defaultVideoDimension,
    ) {
        // The map contains all track dimensions for all participants
        dynascaleLogger.d { "updating dimensions $sessionId $visible $dimensions" }

        // first we make a copy of the dimensions
        val trackDimensionsMap = trackDimensions.value.toMutableMap()

        // next we get or create the dimensions for this participants
        val participantTrackDimensions =
            trackDimensionsMap[sessionId]?.toMutableMap() ?: mutableMapOf()

        // last we get the dimensions for this specific track type
        val oldTrack = participantTrackDimensions[trackType] ?: TrackDimensions(
            dimensions = dimensions,
            visible = visible,
        )
        val newTrack = oldTrack.copy(visible = visible, dimensions = dimensions)
        participantTrackDimensions[trackType] = newTrack

        trackDimensionsMap[sessionId] = participantTrackDimensions

        // Updates are debounced
        trackDimensions.value = trackDimensionsMap
    }

    private fun listenToSubscriberConnection() {
        subscriberListenJob?.cancel()
        subscriberListenJob = coroutineScope.launch {
            // call update participant subscriptions debounced
            subscriber?.let {
                it.state.collect {
                    updatePeerState()
                }
            }
        }
    }

    suspend fun switchSfu(sfuName: String, sfuUrl: String, sfuToken: String, remoteIceServers: List<IceServer>, failedToSwitch: () -> Unit) {
        logger.i { "[switchSfu] from ${this.sfuUrl} to $sfuUrl" }
        val timer = clientImpl.debugInfo.trackTime("call.switchSfu")

        // Prepare SDP
        val getSdp = suspend {
            getSubscriberSdp().description
        }

        // Prepare migration object for SFU socket
        val migration = suspend {
            Migration(
                from_sfu_id = sfuName,
                announced_tracks = getPublisherTracks(getSdp.invoke()),
                subscriptions = subscriptions.value,
            )
        }
        // Create a parallel SFU socket
        sfuConnectionMigrationModule =
            connectionModule.createSFUConnectionModule(
                sfuUrl,
                sessionId,
                sfuToken,
                getSdp,
                sfuFastReconnectListener,
            )

        // Wait until the socket connects - if it fails to connect then return to "Reconnecting"
        // state (to make sure that the full reconnect logic will kick in)
        coroutineScope.launch {
            sfuConnectionMigrationModule!!.sfuSocket.connectionState.collect { it ->
                when (it) {
                    is SocketState.Connected -> {
                        logger.d { "[switchSfu] Migration SFU socket state changed to Connected" }
                        timer.split("SFU socket connected")

                        // Disconnect the old SFU and stop listening to SFU stateflows
                        eventJob?.cancel()
                        errorJob?.cancel()
                        sfuConnectionModule.sfuSocket.cleanup()

                        // Make the new SFU the currently used one
                        setSfuConnectionModule(sfuConnectionMigrationModule!!)
                        sfuConnectionMigrationModule = null

                        // We are connected to the new SFU, change the RtcSession parameters to
                        // match the new SFU
                        this@RtcSession.sfuUrl = sfuUrl
                        this@RtcSession.sfuToken = sfuToken
                        this@RtcSession.remoteIceServers = remoteIceServers
                        this@RtcSession.iceServers = buildRemoteIceServers(remoteIceServers)

                        // reconnect socket listeners
                        listenToSocketEventsAndErrors()

                        var tempSubscriber = subscriber

                        // step 1 setup the peer connections
                        subscriber = createSubscriber()

                        // This makes sure that the new subscriber starts listening to the existing tracks
                        // Without this the peer connection state would stay in NEW
                        setVideoSubscriptions()

                        // Start emiting the new subscriber connection state (used by CallHealthMonitor)
                        listenToSubscriberConnection()

                        // Necessary after SFU migration. This will trigger onNegotiationNeeded
                        publisher?.connection?.restartIce()

                        coroutineScope.launch {
                            subscriber?.state?.collect {
                                if (it == PeerConnectionState.CONNECTED) {
                                    logger.d { "[switchSfu] Migration subscriber state changed to Connected" }
                                    timer.split("Subscriber connected")
                                    tempSubscriber?.let { tempSubscriberValue ->
                                        tempSubscriberValue.connection.close()
                                        tempSubscriber = null
                                    }

                                    onMigrationCompleted.invoke()

                                    timer.finish()
                                    cancel()
                                } else if (it == PeerConnectionState.CLOSED ||
                                    it == PeerConnectionState.DISCONNECTED ||
                                    it == PeerConnectionState.FAILED
                                ) {
                                    logger.d { "[switchSfu] Failed to migrate - subscriber didn't connect ($it)" }
                                    // Something when wrong with the new subscriber connection
                                    // We give up the migration and wait for full reconnect
                                    failedToSwitch()
                                    cancel()
                                }
                            }
                        }

                        updatePeerState()

                        // Only listen for the connection event once
                        cancel()
                    }

                    is SocketState.DisconnectedPermanently -> {
                        logger.d { "[switchSfu] Failed to migrate - SFU socket disconnected permanently ${it.error}" }
                        failedToSwitch()
                        cancel()
                    }

                    is SocketState.DisconnectedTemporarily -> {
                        logger.d { "[switchSfu] Failed to migrate - SFU socket disconnected temporarily ${it.error}" }
                        // We don't wait for the socket to retry during migration
                        // In this case we will fall back to full-reconnect
                        failedToSwitch()
                        cancel()
                    }

                    else -> {
                        // Wait
                    }
                }
            }
        }

        // Connect to SFU socket
        sfuConnectionMigrationModule!!.sfuSocket.connectMigrating(migration) {}
    }
}
