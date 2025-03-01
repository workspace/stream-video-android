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
 * @param defaultDevice
 * @param accessRequestEnabled
 * @param micDefaultOn
 * @param opusDtxEnabled
 * @param redundantCodingEnabled
 * @param speakerDefaultOn
 */


data class AudioSettingsRequest (

    @Json(name = "default_device")
    val defaultDevice: AudioSettingsRequest.DefaultDevice,

    @Json(name = "access_request_enabled")
    val accessRequestEnabled: kotlin.Boolean? = null,

    @Json(name = "mic_default_on")
    val micDefaultOn: kotlin.Boolean? = null,

    @Json(name = "opus_dtx_enabled")
    val opusDtxEnabled: kotlin.Boolean? = null,

    @Json(name = "redundant_coding_enabled")
    val redundantCodingEnabled: kotlin.Boolean? = null,

    @Json(name = "speaker_default_on")
    val speakerDefaultOn: kotlin.Boolean? = null

)

{

    /**
     *
     *
     * Values: speaker,earpiece
     */

    sealed class DefaultDevice(val value: kotlin.String) {
        override fun toString(): String = value

        companion object {
            fun fromString(s: kotlin.String): DefaultDevice = when (s) {
                "speaker" -> Speaker
                "earpiece" -> Earpiece
                else -> Unknown(s)
            }
        }

        object Speaker : DefaultDevice("speaker")
        object Earpiece : DefaultDevice("earpiece")
        data class Unknown(val unknownValue: kotlin.String) : DefaultDevice(unknownValue)

        class DefaultDeviceAdapter : JsonAdapter<DefaultDevice>() {
            @FromJson
            override fun fromJson(reader: JsonReader): DefaultDevice? {
                val s = reader.nextString() ?: return null
                return fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: DefaultDevice?) {
                writer.value(value?.value)
            }
        }
    }



}
