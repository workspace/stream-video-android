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

import android.view.View
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import io.getstream.result.Result.Failure
import io.getstream.result.Result.Success
import io.getstream.video.android.core.call.RtcSession
import io.getstream.video.android.core.events.VideoEvent
import io.getstream.video.android.core.events.VideoEventListener
import io.getstream.video.android.core.model.CallMetadata
import io.getstream.video.android.core.model.IceServer
import io.getstream.video.android.core.model.MuteUsersData
import io.getstream.video.android.core.model.SendReactionData
import io.getstream.video.android.core.model.SfuToken
import io.getstream.video.android.core.model.User
import io.getstream.video.android.core.model.toIceServer
import io.getstream.webrtc.android.ui.VideoTextureViewRenderer
import kotlinx.coroutines.launch
import org.openapitools.client.models.GetCallEdgeServerRequest
import org.openapitools.client.models.GoLiveResponse
import org.openapitools.client.models.SendReactionResponse
import org.openapitools.client.models.StopLiveResponse
import org.openapitools.client.models.UpdateCallResponse
import org.webrtc.RendererCommon
import stream.video.sfu.models.TrackType

public data class SFUConnection(
    internal val callUrl: String,
    internal val sfuToken: SfuToken,
    internal val iceServers: List<IceServer>
)

public class Call(
    internal val client: StreamVideo,
    val type: String,
    val id: String,
    private val token: String = "",
    val user: User,
) {
    private val clientImpl = client as StreamVideoImpl
    var session: RtcSession? = null
    val cid = "$type:$id"
    val state = CallState(this, user)

    val mediaManager by lazy { MediaManagerImpl(client.context) }
    val camera by lazy { mediaManager.camera }
    val microphone by lazy { mediaManager.microphone }
    val speaker by lazy { mediaManager.speaker }

    public var custom: Map<String, Any>? = null

    // should be a stateflow
    private var sfuConnection: SFUConnection? = null

    suspend fun muteAllUsers(): Result<Unit> {
        return muteUsers(MuteUsersData(audio = true, muteAllUsers = true))
    }

    suspend fun join(): Result<RtcSession> {

        /**
         * Alright, how to make this solid
         *
         * - There are 2 methods.
         * -- Client.JoinCall which makes the API call and gets a response
         * -- The whole join process. Which measures latency, uploads it etc
         *
         * Latency measurement needs to be changed
         *
         */

        // step 1. call the join endpoint to get a list of SFUs
        val result = client.joinCall(type, id)
        if (result !is Success) {
            return result as Failure
        }

        // step 2. measure latency
        // TODO: setup the initial call state based on this
        println(result.value.call.settings)

        val edgeUrls = result.value.edges.map { it.latencyUrl }
        // measure latency in parallel
        val measurements = clientImpl.measureLatency(edgeUrls)

        // upload our latency measurements to the server
        val selectEdgeServerResult = client.selectEdgeServer(
            type = type,
            id = id,
            request = GetCallEdgeServerRequest(
                latencyMeasurements = measurements.associate { it.latencyUrl to it.measurements }
            )
        )
        if (selectEdgeServerResult !is Success) {
            return result as Failure
        }

        val credentials = selectEdgeServerResult.value.credentials
        val url = credentials.server.url
        val iceServers =
            selectEdgeServerResult
                .value
                .credentials
                .iceServers
                .map { it.toIceServer() }

        session = RtcSession(
            client = client,
            call = this,
            SFUUrl = url,
            SFUToken = credentials.token,
            connectionModule = (client as StreamVideoImpl).connectionModule,
            remoteIceServers = iceServers,
            latencyResults = measurements.associate { it.latencyUrl to it.measurements }
        )

        client.state.setActiveCall(this)

        session?.let { it.connect() }

        return Success(value = session!!)
    }

    suspend fun sendReaction(data: SendReactionData): Result<SendReactionResponse> {
        return client.sendReaction(type, id, data)
    }

    private val logger by taggedLogger("Call")

    // TODO: review this
    public fun initRenderer(
        videoRenderer: VideoTextureViewRenderer,
        sessionId: String,
        trackType: TrackType,
        onRender: (View) -> Unit = {}
    ) {
        logger.d { "[initRenderer] #sfu; sessionId: $sessionId" }

        // Note this comes from peerConnectionFactory.eglBase
        videoRenderer.init(
            clientImpl.peerConnectionFactory.eglBase.eglBaseContext,
            object : RendererCommon.RendererEvents {
                override fun onFirstFrameRendered() {
                    logger.v { "[initRenderer.onFirstFrameRendered] #sfu; sessionId: $sessionId" }
                    if (trackType != TrackType.TRACK_TYPE_SCREEN_SHARE) {
                        state.updateParticipantTrackSize(
                            sessionId,
                            videoRenderer.measuredWidth,
                            videoRenderer.measuredHeight
                        )
                    }
                    onRender(videoRenderer)
                }

                override fun onFrameResolutionChanged(p0: Int, p1: Int, p2: Int) {
                    logger.v { "[initRenderer.onFrameResolutionChanged] #sfu; sessionId: $sessionId" }

                    if (trackType != TrackType.TRACK_TYPE_SCREEN_SHARE) {
                        state.updateParticipantTrackSize(
                            sessionId,
                            videoRenderer.measuredWidth,
                            videoRenderer.measuredHeight
                        )
                    }
                }
            }
        )
    }

    suspend fun goLive(): Result<GoLiveResponse> {
        return client.goLive(type, id)
    }

    suspend fun stopLive(): Result<StopLiveResponse> {
        return client.stopLive(type, id)
    }

    fun leave() {
        TODO()
    }

    suspend fun end(): Result<Unit> {
        return client.endCall(type, id)
    }

    /** Basic crud operations */
    suspend fun get(): Result<CallMetadata> {
        return client.getOrCreateCall(type, id)
    }

    suspend fun create(): Result<CallMetadata> {
        return client.getOrCreateCall(type, id)
    }

    suspend fun update(): Result<UpdateCallResponse> {
        return client.updateCall(type, id, custom ?: emptyMap())
    }

    /** Permissions */
    suspend fun requestPermissions(vararg permission: String): Result<Unit> {
        return client.requestPermissions(type, id, permission.toList())
    }

    suspend fun startRecording(): Result<Any> {
        return client.startRecording(type, id)
    }

    suspend fun stopRecording(): Result<Any> {
        return client.stopRecording(type, id)
    }

    suspend fun startBroadcasting(): Result<Any> {
        return client.startBroadcasting(type, id)
    }

    suspend fun stopBroadcasting(): Result<Any> {
        return client.stopBroadcasting(type, id)
    }

    private var subscriptions = mutableSetOf<EventSubscription>()

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
        listener: VideoEventListener<VideoEvent>
    ): EventSubscription {
        val sub = EventSubscription(listener)
        subscriptions.add(sub)
        return sub
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

    suspend fun muteUsers(muteUsersData: MuteUsersData): Result<Unit> {
        return client.muteUsers(type, id, muteUsersData)
    }
}
