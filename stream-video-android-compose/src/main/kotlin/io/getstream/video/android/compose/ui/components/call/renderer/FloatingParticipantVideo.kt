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

package io.getstream.video.android.compose.ui.components.call.renderer

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.ParticipantState
import io.getstream.video.android.mock.StreamMockUtils
import io.getstream.video.android.mock.mockCall
import io.getstream.video.android.mock.mockParticipant
import io.getstream.video.android.ui.common.R

/**
 * Represents a floating item used to feature a participant video, usually the local participant.
 *
 * @param call The call containing state.
 * @param participant The participant to render.
 * @param parentBounds Bounds of the parent, used to constrain the component to the parent bounds,
 * when dragging the floating UI around the screen.
 * @param modifier Modifier for styling.
 */
@Composable
public fun FloatingParticipantVideo(
    modifier: Modifier = Modifier
        .size(
            height = VideoTheme.dimens.floatingVideoHeight,
            width = VideoTheme.dimens.floatingVideoWidth
        )
        .clip(VideoTheme.shapes.floatingParticipant),
    call: Call,
    participant: ParticipantState,
    parentBounds: IntSize,
    style: VideoRendererStyle = RegularVideoRendererStyle(isShowingConnectionQualityIndicator = false)
) {
    var videoSize by remember { mutableStateOf(IntSize(0, 0)) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val offset by animateOffsetAsState(targetValue = Offset(offsetX, offsetY), label = "offsets")
    val density = LocalDensity.current

    LaunchedEffect(parentBounds.width) {
        offsetX = 0f
        offsetY = 0f
    }

    val paddingOffset = density.run { VideoTheme.dimens.floatingVideoPadding.toPx() }

    val track by participant.videoTrack.collectAsStateWithLifecycle()

    if (LocalInspectionMode.current) {
        Card(
            elevation = 8.dp,
            modifier = Modifier
                .then(modifier)
                .padding(VideoTheme.dimens.floatingVideoPadding)
                .onGloballyPositioned { videoSize = it.size },
            shape = RoundedCornerShape(16.dp)
        ) {
            Image(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("local_video_renderer"),
                painter = painterResource(id = R.drawable.stream_video_call_sample),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }
        return
    }

    if (track != null) {
        Card(
            elevation = 8.dp,
            modifier = Modifier
                .offset { IntOffset(offset.x.toInt(), offset.y.toInt()) }
                .pointerInput(parentBounds) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()

                        val newOffsetX = (offsetX + dragAmount.x)
                            .coerceAtLeast(
                                -calculateHorizontalOffsetBounds(
                                    parentBounds = parentBounds,
                                    paddingValues = PaddingValues(0.dp),
                                    floatingVideoSize = videoSize,
                                    density = density,
                                    offset = paddingOffset * 2
                                )
                            )
                            .coerceAtMost(
                                0f
                            )

                        val newOffsetY = (offsetY + dragAmount.y)
                            .coerceAtLeast(0f)
                            .coerceAtMost(
                                calculateVerticalOffsetBounds(
                                    parentBounds = parentBounds,
                                    paddingValues = PaddingValues(0.dp),
                                    floatingVideoSize = videoSize,
                                    density = density,
                                    offset = paddingOffset * 2
                                )
                            )

                        offsetX = newOffsetX
                        offsetY = newOffsetY
                    }
                }
                .then(modifier)
                .padding(VideoTheme.dimens.floatingVideoPadding)
                .onGloballyPositioned { videoSize = it.size },
            shape = RoundedCornerShape(16.dp)
        ) {
            ParticipantVideo(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                call = call,
                participant = participant,
                style = style
            )
        }
    }
}

private fun calculateHorizontalOffsetBounds(
    parentBounds: IntSize,
    paddingValues: PaddingValues,
    floatingVideoSize: IntSize,
    density: Density,
    offset: Float
): Float {
    val rightPadding =
        density.run { paddingValues.calculateRightPadding(LayoutDirection.Ltr).toPx() }

    return parentBounds.width - rightPadding - floatingVideoSize.width - offset
}

private fun calculateVerticalOffsetBounds(
    parentBounds: IntSize,
    paddingValues: PaddingValues,
    floatingVideoSize: IntSize,
    density: Density,
    offset: Float
): Float {
    val bottomPadding = density.run { paddingValues.calculateBottomPadding().toPx() }

    return parentBounds.height - bottomPadding - floatingVideoSize.height - offset
}

@Preview
@Composable
private fun LocalVideoContentPreview() {
    StreamMockUtils.initializeStreamVideo(LocalContext.current)
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    VideoTheme {
        FloatingParticipantVideo(
            call = mockCall,
            modifier = Modifier.fillMaxSize(),
            participant = mockParticipant,
            parentBounds = IntSize(screenWidth, screenHeight),
        )
    }
}
