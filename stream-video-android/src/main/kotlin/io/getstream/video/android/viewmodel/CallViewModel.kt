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

package io.getstream.video.android.viewmodel

import android.hardware.camera2.CameraMetadata
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.logging.StreamLog
import io.getstream.video.android.StreamVideo
import io.getstream.video.android.audio.AudioDevice
import io.getstream.video.android.call.CallClient
import io.getstream.video.android.model.Call
import io.getstream.video.android.model.CallEventType
import io.getstream.video.android.model.CallMetadata
import io.getstream.video.android.model.CallParticipantState
import io.getstream.video.android.model.CallSettings
import io.getstream.video.android.model.CallType
import io.getstream.video.android.model.CallUser
import io.getstream.video.android.model.state.StreamDate
import io.getstream.video.android.utils.Failure
import io.getstream.video.android.utils.Success
import io.getstream.video.android.utils.flatMap
import io.getstream.video.android.utils.map
import io.getstream.video.android.utils.onError
import io.getstream.video.android.utils.onSuccess
import io.getstream.video.android.utils.onSuccessSuspend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.UUID
import io.getstream.video.android.model.state.StreamCallState as State

public class CallViewModel(
    private val streamVideo: StreamVideo,
) : ViewModel() {

    private val logger = StreamLog.getLogger("Call:ViewModel")

    private val _callState: MutableStateFlow<Call?> =
        MutableStateFlow(null)
    public val callState: StateFlow<Call?> = _callState

    private var _isVideoInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    public val isVideoInitialized: StateFlow<Boolean> = _isVideoInitialized

    private val hasVideoPermission: MutableStateFlow<Boolean> =
        MutableStateFlow(input.hasVideoPermission)
    private val isVideoEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)

    public val isVideoOn: Flow<Boolean> =
        hasVideoPermission.combine(isVideoEnabled) { hasPermission, videoEnabled ->
            hasPermission && videoEnabled
        }

    private val hasAudioPermission: MutableStateFlow<Boolean> =
        MutableStateFlow(input.hasAudioPermission)
    private val isAudioEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)

    public val isAudioOn: Flow<Boolean> =
        hasAudioPermission.combine(isAudioEnabled) { hasPermission, audioEnabled ->
            hasPermission && audioEnabled
        }

    public val participantList: Flow<List<CallParticipantState>> =
        callState.filterNotNull().flatMapLatest { it.callParticipants }

    public val activeSpeakers: Flow<List<CallParticipantState>> =
        callState.filterNotNull().flatMapLatest { it.activeSpeakers }

    public val localParticipant: Flow<CallParticipantState> =
        callState.filterNotNull().flatMapLatest { it.localParticipant }

    private val _isShowingParticipantsInfo = MutableStateFlow(false)
    public val isShowingParticipantsInfo: StateFlow<Boolean> = _isShowingParticipantsInfo

    private val _isShowingSettings = MutableStateFlow(false)
    public val isShowingSettings: StateFlow<Boolean> = _isShowingSettings

    public val streamCallState: StateFlow<State> get() = streamVideo.callState

    private val _callType: MutableStateFlow<CallType> = MutableStateFlow(CallType.VIDEO)
    public val callType: StateFlow<CallType> = _callType

    private val _callId: MutableStateFlow<String> = MutableStateFlow(value = "")
    public val callId: StateFlow<String> = _callId

    private val _participants: MutableStateFlow<List<CallUser>> = MutableStateFlow(emptyList())
    public val participants: StateFlow<List<CallUser>> = _participants

    private val _isMicrophoneEnabled = MutableStateFlow(false)
    public val isMicrophoneEnabled: StateFlow<Boolean> = _isMicrophoneEnabled

    private val _isCameraEnabled = MutableStateFlow(false)
    public val isCameraEnabled: StateFlow<Boolean> = _isCameraEnabled

    private lateinit var client: CallClient

    private var prevState: State = State.Idle

    init {
        viewModelScope.launch {
            streamVideo.callState.collect { state ->
                logger.i { "[observeState] state: $state" }
                when (state) {
                    is State.Idle -> {
                        clearState()
                    }
                    is State.Incoming -> {
                        _callType.value = CallType.fromType(state.callGuid.type)
                        _participants.value = state.users.values.toList()
                    }
                    is State.Starting -> {
                        _callType.value = CallType.fromType(state.callGuid.type)
                        _callId.value = state.callGuid.id
                    }
                    is State.Outgoing -> {
                        _callType.value = CallType.fromType(state.callGuid.type)
                        _callId.value = state.callGuid.id
                        _participants.value = state.users.values
                            .filter { it.id != streamVideo.getUser().id }
                            .toList()
                        joinCall()
                    }
                    is State.Joining -> {

                    }
                    is State.Connected -> {

                    }
                    is State.Connecting -> {

                    }
                    is State.Drop -> {

                    }
                }
                prevState = state
            }
        }
    }

    public fun connectToCall(callSettings: CallSettings) {
        logger.d { "[createCall] input: $callSettings" }
        // this._callState.value = videoClient.getCall(callId) TODO - load details

        // TODO CallClient is supposed to live longer than VM
        //  VM can be destroyed while the call is still running in the BG
        viewModelScope.launch {
            logger.d { "[connectToCall] state: ${streamCallState.value}" }
            withTimeout(30_000) {
                val state = streamCallState.first { it is State.InCall } as State.InCall
                logger.v { "[connectToCall] received: ${streamCallState.value}" }
                client = streamVideo.createCallClient(
                    state.callUrl.removeSuffix("/twirp"),
                    state.userToken,
                    state.iceServers,
                )
                _isVideoInitialized.value = true
                initializeCall(callSettings = callSettings)
            }
        }
    }

    private suspend fun initializeCall(callSettings: CallSettings) {
        val callResult = client.connectToCall(
            UUID.randomUUID().toString(),
            callSettings
        )

        when (callResult) {
            is Success -> {
                val call = callResult.data
                _callState.value = call
                isVideoEnabled.value = callSettings.videoOn
                isAudioEnabled.value = callSettings.audioOn

                val isVideoOn = isVideoOn.firstOrNull() ?: false

                if (callSettings.autoPublish && isVideoOn) {
                    client.startCapturingLocalVideo(CameraMetadata.LENS_FACING_FRONT)
                }
            }
            is Failure -> {
                // TODO - show error to user
            }
        }
    }

    public fun toggleCamera(enabled: Boolean) {
        client.setCameraEnabled(enabled)
    }

    public fun toggleMicrophone(enabled: Boolean) {
        client.setMicrophoneEnabled(enabled)
    }

    /**
     * Flips the camera for the current participant if possible.
     */
    public fun flipCamera() {
        client.flipCamera()
    }

    /**
     * Sets the flag used to display the settings menu in the UI to true.
     */
    public fun showSettings() {
        _isShowingSettings.value = true
    }

    /**
     * Attempts to reconnect to the video room, by cleaning the state, disconnecting, canceling any
     * jobs and finally reinitializing.
     */
    public fun reconnect() {
//        val room = _roomState.value ?: return
//
//        _primarySpeaker.value = null
//        room.disconnect()
//        viewModelScope.coroutineContext.cancelChildren()
//
//        viewModelScope.launch {
//            val call = _callState.value
//            val url = _urlState.value
//            val token = _tokenState.value
//
//            init(room, call!!, url, token)
//        }
    }

    override fun onCleared() {
        super.onCleared()
        clearState()
    }

    /**
     * Sets the flag used to display participants info menu in the UI to true.
     */
    public fun showParticipants() {
        this._isShowingParticipantsInfo.value = true
    }

    /**
     * Drops the call by sending a cancel event, which informs other users.
     */
    public fun cancelCall() {
        val state = streamVideo.callState.value
        if (state !is State.Active) {
            logger.w { "[cancelCall] rejected (state is not Active): $state" }
            return
        }
        viewModelScope.launch {
            logger.d { "[cancelCall] state: $state" }
            streamVideo.cancelCall(state.callGuid.cid)
        }
    }

    /**
     * @return A [List] of [AudioDevice] that can be used for playback.
     */
    public fun getAudioDevices(): List<AudioDevice> {
        return client.getAudioDevices()
    }

    /**
     * Clears the state of the call and disposes of the CallClient and Call instances.
     */
    public fun clearState() {
        logger.i { "[leaveCall] no args" }
        streamVideo.clearCallState()
        val room = _callState.value ?: return

        room.disconnect()
        _callState.value = null
        isVideoEnabled.value = false
        isAudioEnabled.value = false
        hasAudioPermission.value = false
        hasVideoPermission.value = false
        _isVideoInitialized.value = false
        dismissOptions()
    }

    /**
     * Resets the state of two popup UI flags.
     */
    public fun dismissOptions() {
        this._isShowingSettings.value = false
        this._isShowingParticipantsInfo.value = false
    }

    /**
     * Selects an audio device to be used for playback.
     *
     * @param device The device to use.
     */
    public fun selectAudioDevice(device: AudioDevice) {
        client.selectAudioDevice(device)
    }

    public fun acceptCall() {
        val state = streamVideo.callState.value
        if (state !is State.Incoming || state.acceptedByMe) {
            logger.w { "[acceptCall] rejected (state is not unaccepted Incoming): $state" }
            return
        }
        logger.d { "[acceptCall] state: $state" }
        viewModelScope.launch {
            streamVideo.joinCall(state.callGuid.type, state.callGuid.id)
                .flatMap { joined ->
                    logger.v { "[acceptCall] joined: $joined" }
                    streamVideo.sendEvent(
                        callCid = joined.call.cid,
                        eventType = CallEventType.ACCEPTED
                    ).map { joined }
                }
                .onSuccess {
                    logger.v { "[acceptCall] completed: $it" }
                    //TODO
                    // streamRouter.navigateToCall(callInput = it.toCallInput(), finishCurrent = true)
                }
                .onError {
                    logger.e { "[acceptCall] failed: $it" }
                    rejectCall()
                }
        }
    }

    public fun rejectCall() {
        val state = streamVideo.callState.value
        if (state !is State.Incoming || state.acceptedByMe) {
            logger.w { "[declineCall] rejected (state is not unaccepted Incoming): $state" }
            return
        }
        logger.d { "[declineCall] state: $state" }
        viewModelScope.launch {
            val result = streamVideo.rejectCall(state.callGuid.cid)
            logger.d { "[declineCall] result: $result" }
        }
    }

    public fun hangUpCall() {
        val state = streamVideo.callState.value
        if (state !is State.Active) {
            logger.w { "[hangUpCall] rejected (state is not Active): $state" }
            return
        }
        logger.d { "[hangUpCall] state: $state" }
        viewModelScope.launch {
            streamVideo.cancelCall(state.callGuid.cid)
        }
    }

    private fun joinCall() {
        val state = streamVideo.callState.value
        if (state !is State.Outgoing || !state.acceptedByCallee) {
            logger.w { "[joinCall] rejected (state is not accepted Outgoing): $state" }
            return
        }
        logger.d { "[joinCall] state: $state" }
        viewModelScope.launch {
            val joinResult = streamVideo.joinCall(
                state.toMetadata()
            )

            joinResult.onSuccessSuspend { response ->
                /* TODO
                streamRouter.navigateToCall(
                    callInput = CallInput(
                        callCid = response.call.cid,
                        callType = response.call.type,
                        callId = response.call.id,
                        callUrl = response.callUrl,
                        userToken = response.userToken,
                        iceServers = response.iceServers
                    ),
                    finishCurrent = true
                )*/
            }
            joinResult.onError {
                logger.e { "[joinCall] failed: $it" }
                //TODO
                // streamRouter.onCallFailed(it.message)
            }
        }
    }

    public fun onMicrophoneChanged(microphoneEnabled: Boolean) {
        logger.d { "[onMicrophoneChanged] microphoneEnabled: $microphoneEnabled" }
        this._isMicrophoneEnabled.value = microphoneEnabled
    }

    public fun onVideoChanged(videoEnabled: Boolean) {
        logger.d { "[onVideoChanged] videoEnabled: $videoEnabled" }
        this._isCameraEnabled.value = videoEnabled
    }

    private fun State.Outgoing.toMetadata(): CallMetadata =
        CallMetadata(
            cid = callGuid.cid,
            type = callGuid.type,
            id = callGuid.id,
            users = users,
            members = members,
            createdAt = (createdAt as? StreamDate.Specified)?.date?.time ?: 0,
            updatedAt = (updatedAt as? StreamDate.Specified)?.date?.time ?: 0,
            createdByUserId = createdByUserId,
            broadcastingEnabled = broadcastingEnabled,
            recordingEnabled = recordingEnabled,
            extraData = emptyMap()
        )
}
