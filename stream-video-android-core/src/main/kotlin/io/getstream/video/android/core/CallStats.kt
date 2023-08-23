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

package io.getstream.video.android.core

import android.os.Build
import io.getstream.log.taggedLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.webrtc.CameraEnumerationAndroid
import org.webrtc.RTCStats
import org.webrtc.RTCStatsReport
import stream.video.sfu.models.TrackType

public class PeerConnectionStats {
    internal val _resolution: MutableStateFlow<String> = MutableStateFlow("")
    val resolution: StateFlow<String> = _resolution

    internal val _qualityDropReason: MutableStateFlow<String> = MutableStateFlow("")
    val qualityDropReason: StateFlow<String> = _qualityDropReason

    internal val _jitterInMs: MutableStateFlow<Int> = MutableStateFlow(0)
    val jitterInMs: StateFlow<Int> = _jitterInMs

    internal val _bitrateKbps: MutableStateFlow<Float> = MutableStateFlow(0F)
    val bitrateKbps: StateFlow<Float> = _bitrateKbps
}

public data class LocalStats(
    val resolution: CameraEnumerationAndroid.CaptureFormat?,
    val availableResolutions: List<CameraEnumerationAndroid.CaptureFormat>?,
    val maxResolution: CameraEnumerationAndroid.CaptureFormat?,
    val sfu: String,
    val os: String,
    val sdkVersion: String,
    val deviceModel: String,
)

public class CallStats(val call: Call, val callScope: CoroutineScope) {
    private val logger by taggedLogger("CallStats")

    private val supervisorJob = SupervisorJob()
    private val scope = CoroutineScope(callScope.coroutineContext + supervisorJob)
    // TODO: cleanup the scope

    val publisher = PeerConnectionStats()
    val subscriber = PeerConnectionStats()
    val _local = MutableStateFlow<LocalStats?>(null)
    val local: StateFlow<LocalStats?> = _local

    fun updateFromRTCStats(stats: RTCStatsReport?, isPublisher: Boolean = true) {
        if (stats == null) return
        // also see https://github.com/GetStream/stream-video-js/blob/main/packages/client/src/stats/state-store-stats-reporter.ts

        val skipTypes = listOf("codec", "certificate", "data-channel")
        val trackToParticipant = call.session?.trackIdToParticipant?.value ?: emptyMap()
        val displayingAt = call.session?.trackDimensions?.value ?: emptyMap()

        val statGroups = mutableMapOf<String, MutableList<RTCStats>>()

        for (entry in stats.statsMap) {
            val stat = entry.value

            val type = stat.type
            if (type in skipTypes) continue

            val statGroup = if (type == "inbound-rtp") {
                "$type:${stat.members["kind"]}"
            } else if (type == "track") {
                "$type:${stat.members["kind"]}"
            } else if (type == "outbound-rtp") {
                val rid = stat.members["rid"] ?: "missing"
                "$type:${stat.members["kind"]}:$rid"
            } else {
                type
            }

            if (statGroup != null) {
                if (statGroup !in statGroups) {
                    statGroups[statGroup] = mutableListOf()
                }
                statGroups[statGroup]?.add(stat)
            }

            statGroups["outbound-rtp:video:f"]?.firstOrNull()?.let {
                val qualityLimit = it.members["qualityLimitationReason"] as? String
                val width = it.members["frameWidth"] as? Long
                val height = it.members["frameHeight"] as? Long
                val fps = it.members["framesPerSecond"] as? Double
                val deviceLatency = it.members["totalPacketSendDelay"] as? Double
                // fir pli nack are also interesting
                publisher._qualityDropReason.value = qualityLimit ?: ""
            }

            statGroups["inbound-rtp:video"]?.firstOrNull()?.let {
                val jitter = it.members["jitter"] as Double
                subscriber._jitterInMs.value = (jitter * 1000).toInt()
            }
            statGroups["track:video"]?.forEach {
                // trackIdentifier is a random UUID generated by the browser
                // map to a participant?
                val trackId = it.members["trackIdentifier"]
                val participantId = trackToParticipant[trackId]
                val participantVideo = displayingAt[participantId] ?: emptyMap()
                val visibleAt = participantVideo[TrackType.TRACK_TYPE_VIDEO]
                val freezeInSeconds = it.members["totalFreezesDuration"]
                val frameWidth = it.members["frameWidth"] as? Long
                val frameHeight = it.members["frameHeight"] as? Long
                val received = it.members["framesReceived"] as? Long
                val duration = it.members["totalFramesDuration"] as? Long

                if (participantId != null) {
                    logger.i {
                        "receiving video for $participantId at $frameWidth: ${it.members["frameWidth"]} and rendering it at ${visibleAt?.dimensions?.width} visible: ${visibleAt?.visible}"
                    }
                }
            }
        }

        statGroups.forEach {
            logger.i { "statgroup ${it.key}:${it.value}" }
        }

        scope.launch {
            val toMap = mutableMapOf<String, Any>()
            toMap["data"] = stats.statsMap
            call.sendStats(toMap)
        }
    }

    fun updateLocalStats() {
        val displayingAt = call.session?.trackDimensions?.value ?: emptyMap()
        val resolution = call.camera.resolution.value
        val availableResolutions = call.camera.availableResolutions.value
        val maxResolution = availableResolutions.maxByOrNull { it.width * it.height }

        val sfu = call.session?.sfuUrl

        val version = BuildConfig.STREAM_VIDEO_VERSION
        val osVersion = Build.VERSION.RELEASE ?: ""

        val vendor = Build.MANUFACTURER ?: ""
        val model = Build.MODEL ?: ""
        val deviceModel = ("$vendor $model").trim()

        val local = LocalStats(
            resolution = resolution,
            availableResolutions = availableResolutions,
            maxResolution = maxResolution,
            sfu = sfu ?: "",
            os = osVersion,
            sdkVersion = version,
            deviceModel = deviceModel,
        )
        _local.value = local

        scope.launch {
            val toMap = mutableMapOf<String, Any>()
            toMap["availableResolutions"] = availableResolutions
            toMap["maxResolution"] = maxResolution ?: ""
            toMap["displayingAt"] = displayingAt
            call.sendStats(toMap)
        }
    }
}
