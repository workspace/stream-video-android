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

package io.getstream.video.android.compose.ui.components.call

import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.participants.ParticipantIndicatorIcon
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.call.state.CallAction
import io.getstream.video.android.core.call.state.ShowCallParticipantInfo
import io.getstream.video.android.mock.StreamPreviewDataUtils
import io.getstream.video.android.mock.previewCall
import io.getstream.video.android.ui.common.R

/**
 * Represents the default AppBar that's shown in calls. Exposes handlers for the two default slot
 * component implementations (leading and trailing).
 *
 * Exposes slots required to customize the look and feel.
 *
 * @param call The call that contains all the participants state and tracks.
 * @param modifier Modifier for styling.
 * @param onBackPressed Handler when the user taps on the default leading content slot.
 * @param leadingContent The leading content, by default [DefaultCallAppBarLeadingContent].
 * @param centerContent The center content, by default [DefaultCallAppBarCenterContent].
 * @param trailingContent The trailing content, by default [DefaultCallAppBarTrailingContent].
 * */
@Composable
public fun CallAppBar(
    call: Call,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    onCallAction: (CallAction) -> Unit = {},
    title: String = stringResource(id = R.string.stream_video_default_app_bar_title),
    leadingContent: (@Composable RowScope.() -> Unit)? = {
        DefaultCallAppBarLeadingContent(onBackPressed)
    },
    centerContent: (@Composable (RowScope.() -> Unit))? = {
        DefaultCallAppBarCenterContent(call, title)
    },
    trailingContent: (@Composable RowScope.() -> Unit)? = {
        DefaultCallAppBarTrailingContent(
            call = call,
            onCallAction = onCallAction,
        )
    },
) {
    val orientation = LocalConfiguration.current.orientation
    val height = if (orientation == ORIENTATION_LANDSCAPE) {
        VideoTheme.dimens.landscapeTopAppBarHeight
    } else {
        VideoTheme.dimens.topAppbarHeight
    }

    val endPadding = if (orientation == ORIENTATION_LANDSCAPE) {
        VideoTheme.dimens.controlActionsHeight
    } else {
        VideoTheme.dimens.callAppBarPadding
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.2f),
                        Color.Transparent,
                    ),
                ),
            )
            .padding(
                start = VideoTheme.dimens.callAppBarPadding,
                top = VideoTheme.dimens.callAppBarPadding,
                bottom = VideoTheme.dimens.callAppBarPadding,
                end = endPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingContent?.invoke(this)

        centerContent?.invoke(this)

        trailingContent?.invoke(this)
    }
}

/**
 * Default leading slot, representing the back button.
 */
@Composable
internal fun DefaultCallAppBarLeadingContent(
    onBackButtonClicked: () -> Unit,
) {
    IconButton(
        onClick = onBackButtonClicked,
        modifier = Modifier.padding(
            start = VideoTheme.dimens.callAppBarLeadingContentSpacingStart,
            end = VideoTheme.dimens.callAppBarLeadingContentSpacingEnd,
        ),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.stream_video_ic_arrow_back),
            contentDescription = stringResource(
                id = R.string.stream_video_back_button_content_description,
            ),
            tint = VideoTheme.colors.callDescription,
        )
    }
}

/**
 * Default center slot, representing the call title.
 */
@Composable
internal fun RowScope.DefaultCallAppBarCenterContent(call: Call, title: String) {
    val isReconnecting by call.state.isReconnecting.collectAsStateWithLifecycle()
    val isRecording by call.state.recording.collectAsStateWithLifecycle()

    if (isRecording) {
        Box(
            modifier = Modifier
                .size(VideoTheme.dimens.callAppBarRecordingIndicatorSize)
                .align(Alignment.CenterVertically)
                .clip(CircleShape)
                .background(VideoTheme.colors.errorAccent),
        )

        Spacer(modifier = Modifier.width(6.dp))
    }

    Text(
        modifier = Modifier
            .weight(1f)
            .padding(
                start = VideoTheme.dimens.callAppBarCenterContentSpacingStart,
                end = VideoTheme.dimens.callAppBarCenterContentSpacingEnd,
            ),
        text = if (isReconnecting) {
            stringResource(id = R.string.stream_video_call_reconnecting)
        } else if (isRecording) {
            stringResource(id = R.string.stream_video_call_recording)
        } else {
            title
        },
        fontSize = VideoTheme.dimens.topAppbarTextSize,
        color = VideoTheme.colors.callDescription,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start,
    )
}

/**
 * Default trailing content slot, representing an icon to show the call participants menu.
 */
@Composable
internal fun DefaultCallAppBarTrailingContent(
    call: Call,
    onCallAction: (CallAction) -> Unit,
) {
    val participants by call.state.participants.collectAsStateWithLifecycle()

    ParticipantIndicatorIcon(
        number = participants.size,
        onClick = { onCallAction(ShowCallParticipantInfo) },
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CallTopAppbarPreview() {
    StreamPreviewDataUtils.initializeStreamVideo(LocalContext.current)
    VideoTheme {
        CallAppBar(call = previewCall)
    }
}
