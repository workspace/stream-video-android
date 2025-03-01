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

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapitools.client.models

import org.openapitools.client.models.AudioSettings
import org.openapitools.client.models.BackstageSettings
import org.openapitools.client.models.BroadcastSettings
import org.openapitools.client.models.GeofenceSettings
import org.openapitools.client.models.RecordSettings
import org.openapitools.client.models.RingSettings
import org.openapitools.client.models.ScreensharingSettings
import org.openapitools.client.models.TranscriptionSettings
import org.openapitools.client.models.VideoSettings




import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import org.openapitools.client.infrastructure.Serializer

/**
 *
 *
 * @param audio
 * @param backstage
 * @param broadcasting
 * @param geofencing
 * @param recording
 * @param ring
 * @param screensharing
 * @param transcription
 * @param video
 */


data class CallSettingsResponse (

    @Json(name = "audio")
    val audio: AudioSettings,

    @Json(name = "backstage")
    val backstage: BackstageSettings,

    @Json(name = "broadcasting")
    val broadcasting: BroadcastSettings,

    @Json(name = "geofencing")
    val geofencing: GeofenceSettings,

    @Json(name = "recording")
    val recording: RecordSettings,

    @Json(name = "ring")
    val ring: RingSettings,

    @Json(name = "screensharing")
    val screensharing: ScreensharingSettings,

    @Json(name = "transcription")
    val transcription: TranscriptionSettings,

    @Json(name = "video")
    val video: VideoSettings

)
