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

data class RtcRemoteOutboundRtpVideoStreamStats(
    override val id: String?,
    override val type: String?,
    override val timestampUs: Double?,
    override val ssrc: Long?,
    override val kind: String?,
    override val transportId: String?,
    override val codecId: String?,
    override val packetsSent: BigInteger?,
    override val bytesSent: BigInteger?,
    override val localId: String?,
    override val remoteTimestamp: Double?,
    override val reportsSent: BigInteger?,
    override val roundTripTime: Double?,
    override val totalRoundTripTime: Double?,
    override val roundTripTimeMeasurements: BigInteger?,
) : RtcRemoteOutboundRtpStreamStats {

    companion object {
        const val LOCAL_ID = "localId"
        const val REMOTE_TIMESTAMP = "remoteTimestamp"
        const val REPORTS_SENT = "reportsSent"
        const val ROUND_TRIP_TIME = "roundTripTime"
        const val TOTAL_ROUND_TRIP_TIME = "totalRoundTripTime"
        const val ROUND_TRIP_TIME_MEASUREMENTS = "roundTripTimeMeasurements"
    }
}
