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

package io.getstream.video.android.webrtc

import io.getstream.video.android.audio.AudioDevice
import io.getstream.video.android.model.Call
import io.getstream.video.android.model.CallSettings
import io.getstream.video.android.utils.Result

internal interface WebRTCClient {

    fun clear()

    suspend fun connectToCall(sessionId: String, autoPublish: Boolean, callSettings: CallSettings): Result<Call>

    fun startCapturingLocalVideo(position: Int)

    fun setCameraEnabled(isEnabled: Boolean)

    fun setMicrophoneEnabled(isEnabled: Boolean)

    fun flipCamera()

    fun getAudioDevices(): List<AudioDevice>

    fun selectAudioDevice(device: AudioDevice)
}
