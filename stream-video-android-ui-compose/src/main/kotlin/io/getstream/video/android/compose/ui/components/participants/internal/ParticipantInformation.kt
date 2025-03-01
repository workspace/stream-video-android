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

package io.getstream.video.android.compose.ui.components.participants.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.core.MemberState
import io.getstream.video.android.core.model.CallStatus
import io.getstream.video.android.core.utils.toCallUser
import io.getstream.video.android.mock.StreamPreviewDataUtils
import io.getstream.video.android.mock.previewMemberListState
import io.getstream.video.android.ui.common.util.buildLargeCallText
import io.getstream.video.android.ui.common.util.buildSmallCallText

@Composable
public fun ParticipantInformation(
    callStatus: CallStatus,
    participants: List<MemberState>,
    isVideoType: Boolean = true,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val context = LocalContext.current
        val callUsers by remember { derivedStateOf { participants.map { it.toCallUser() } } }
        val text = if (participants.size < 3) {
            buildSmallCallText(context, callUsers)
        } else {
            buildLargeCallText(context, callUsers)
        }

        val fontSize = if (participants.size == 1) {
            VideoTheme.dimens.directCallUserNameTextSize
        } else {
            VideoTheme.dimens.groupCallUserNameTextSize
        }

        Text(
            modifier = Modifier.padding(horizontal = VideoTheme.dimens.participantsTextPadding),
            text = text,
            fontSize = fontSize,
            color = VideoTheme.colors.callDescription,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(VideoTheme.dimens.callStatusParticipantsMargin))

        val callType = if (isVideoType) {
            "video"
        } else {
            "audio"
        }

        Text(
            modifier = Modifier.alpha(VideoTheme.dimens.onCallStatusTextAlpha),
            text = when (callStatus) {
                CallStatus.Incoming -> stringResource(
                    id = io.getstream.video.android.ui.common.R.string.stream_video_call_status_incoming,
                    callType.capitalize(Locale.current),
                )

                CallStatus.Outgoing -> stringResource(
                    id = io.getstream.video.android.ui.common.R.string.stream_video_call_status_outgoing,
                )

                is CallStatus.Calling -> callStatus.duration
            },
            style = VideoTheme.typography.body,
            fontSize = VideoTheme.dimens.onCallStatusTextSize,
            fontWeight = FontWeight.Bold,
            color = VideoTheme.colors.callDescription,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun ParticipantInformationPreview() {
    StreamPreviewDataUtils.initializeStreamVideo(LocalContext.current)
    VideoTheme {
        ParticipantInformation(
            isVideoType = true,
            callStatus = CallStatus.Incoming,
            participants = previewMemberListState,
        )
    }
}
