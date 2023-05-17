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

package io.getstream.video.chat_with_video_starter.ui.call

import io.getstream.video.android.compose.ui.AbstractComposeCallActivity
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.viewmodel.CallViewModelFactory

class CallActivity : AbstractComposeCallActivity() {

    /** Provides a custom factory for the ViewModel, that provides fake users for invites. */
    override fun getCallViewModelFactory(): CallViewModelFactory {
        return CallViewModelFactory(
            call = StreamVideo.instance().call("default", "123"),
        )
    }
}
