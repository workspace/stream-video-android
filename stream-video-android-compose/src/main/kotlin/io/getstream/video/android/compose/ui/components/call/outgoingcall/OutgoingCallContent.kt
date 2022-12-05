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

package io.getstream.video.android.compose.ui.components.call.outgoingcall

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.getstream.video.android.call.state.CallAction
import io.getstream.video.android.call.state.CallMediaState
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.background.CallBackground
import io.getstream.video.android.compose.ui.components.call.CallAppBar
import io.getstream.video.android.compose.ui.components.call.outgoingcall.internal.OutgoingCallDetails
import io.getstream.video.android.compose.ui.components.call.outgoingcall.internal.OutgoingGroupCallOptions
import io.getstream.video.android.compose.ui.components.call.outgoingcall.internal.OutgoingSingleCallOptions
import io.getstream.video.android.compose.ui.components.mock.mockParticipant
import io.getstream.video.android.model.CallType
import io.getstream.video.android.model.CallUser
import io.getstream.video.android.viewmodel.CallViewModel

/**
 * Represents the Outgoing Call state and UI, when the user is calling other people.
 *
 * @param viewModel The [CallViewModel] used to provide state and various handlers in the call.
 * @param modifier Modifier for styling.
 * @param onBackPressed Handler when the user taps on the back button.
 * @param onCallAction Handler when the user clicks on some of the call controls.
 */
@Composable
public fun OutgoingCallContent(
    viewModel: CallViewModel,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onCallAction: (CallAction) -> Unit = viewModel::onCallAction,
) {
    val callType: CallType by viewModel.callType.collectAsState()
    val participants: List<CallUser> by viewModel.participants.collectAsState()
    val callMediaState: CallMediaState by viewModel.callMediaState.collectAsState()

    OutgoingCall(
        callType = callType,
        participants = participants,
        callMediaState = callMediaState,
        modifier = modifier,
        onBackPressed = onBackPressed,
        onCallAction = onCallAction
    )
}

/**
 * Stateless variant of the Outgoing call UI, which you can use to build your own custom logic that
 * powers the state and handlers.
 *
 * @param callType The type of call, Audio or Video.
 * @param participants People participating in the call.
 * @param callMediaState The state of current user media (camera on, audio on, etc.).
 * @param modifier Modifier for styling.
 * @param onBackPressed Handler when the user taps on back.
 * @param onCallAction Handler when the user clicks on some of the call controls.
 */
@Composable
public fun OutgoingCall(
    callType: CallType,
    participants: List<CallUser>,
    callMediaState: CallMediaState,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onCallAction: (CallAction) -> Unit,
) {
    CallBackground(
        modifier = modifier,
        participants = participants,
        callType = callType,
        isIncoming = false
    ) {

        Column {

            CallAppBar(onBackPressed = onBackPressed, onCallInfoSelected = onCallInfoSelected)

            val topPadding = if (participants.size == 1 || callType == CallType.VIDEO) {
                VideoTheme.dimens.singleAvatarAppbarPadding
            } else {
                VideoTheme.dimens.avatarAppbarPadding
            }

            OutgoingCallDetails(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = topPadding),
                participants = participants,
                callType = callType
            )
        }

        if (participants.size == 1) {
            OutgoingSingleCallOptions(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 44.dp),
                callMediaState = callMediaState,
                onCallAction = onCallAction
            )
        } else {
            OutgoingGroupCallOptions(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 44.dp),
                callMediaState = callMediaState,
                onCallAction = onCallAction
            )
        }
    }
}

@Preview
@Composable
private fun OutgoingCallPreview() {
    VideoTheme {
        OutgoingCall(
            callType = CallType.VIDEO,
            participants = listOf(
                mockParticipant.let {
                    CallUser(
                        id = it.id,
                        name = it.name,
                        role = it.role,
                        state = null,
                        imageUrl = it.profileImageURL ?: "",
                        createdAt = null,
                        updatedAt = null,
                        teams = emptyList()
                    )
                }
            ),
            callMediaState = CallMediaState(),
            onCallAction = {}
        )
    }
}
