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

package io.getstream.video.android.core.call.video

import android.graphics.Bitmap
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoFrame

/**
 * Do not create instances directly. Use [BitmapVideoFilter] or [RawVideoFilter]
 */
open class VideoFilter internal constructor()

/**
 * A filter that provides a Bitmap of each frame. It's less performant than using the
 * [RawVideoFilter] because we do YUV<->ARGB conversions internally.
 */
abstract class BitmapVideoFilter : VideoFilter() {
    abstract fun filter(bitmap: Bitmap)
}

/**
 * Raw [VideoFrame] data from WebRTC - use for maximum performance, but usually requires more
 * complex work.
 */
abstract class RawVideoFilter : VideoFilter() {
    abstract fun filter(
        videoFrame: VideoFrame,
        surfaceTextureHelper: SurfaceTextureHelper,
    ): VideoFrame
}
