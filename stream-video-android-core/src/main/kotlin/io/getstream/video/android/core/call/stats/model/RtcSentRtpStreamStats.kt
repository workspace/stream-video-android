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

package io.getstream.video.android.core.call.stats.model

import java.math.BigInteger

sealed interface RtcSentRtpStreamStats : RtcRtpStreamStats {

    val packetsSent: BigInteger?
    val bytesSent: BigInteger?

    companion object {
        const val SSRC = RtcRtpStreamStats.SSRC
        const val KIND = RtcRtpStreamStats.KIND
        const val TRANSPORT_ID = RtcRtpStreamStats.TRANSPORT_ID
        const val CODEC_ID = RtcRtpStreamStats.CODEC_ID
        const val PACKETS_SENT = "packetsSent"
        const val BYTES_SENT = "packetsLost"
    }
}
