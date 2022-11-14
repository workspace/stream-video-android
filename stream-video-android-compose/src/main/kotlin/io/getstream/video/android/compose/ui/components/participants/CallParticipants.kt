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

package io.getstream.video.android.compose.ui.components.participants

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.getstream.video.android.compose.ui.components.participants.internal.Participants
import io.getstream.video.android.model.Call

/**
 * Renders all the CallParticipants, based on the number of people in a call and the call state.
 *
 * @param call The call that contains all the participants state and tracks.
 * @param modifier Modifier for styling.
 * @param onRender Handler when each of the Video views render their first frame.
 */
@Composable
public fun CallParticipants(
    call: Call,
    modifier: Modifier = Modifier,
    onRender: (View) -> Unit = {}
) {
    Box(modifier = modifier) {
        Participants(
            modifier = Modifier.fillMaxSize(),
            call = call,
            onRender = onRender
        )

//        val localParticipantState by room.localParticipant.collectAsState(initial = null)
//        val currentLocal = localParticipantState
//
//        if (currentLocal != null) { // TODO - fix once we import correct Surface views
//            FloatingParticipantItem(
//                room = room,
//                callParticipant = currentLocal,
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .size(height = 150.dp, width = 125.dp)
//                    .padding(16.dp)
//                    .clip(RoundedCornerShape(16.dp)),
//                onRender = {
//                    (it as? SurfaceViewRenderer)?.apply {
//                        setZOrderOnTop(true)
//                        setZOrderMediaOverlay(true)
//                    }
//                }
//            )
//        }
    }
}