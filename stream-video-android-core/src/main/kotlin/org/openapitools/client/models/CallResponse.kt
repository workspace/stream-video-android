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

import com.squareup.moshi.Json

/**
 * Represents a call
 *
 * @param broadcastEgress * @param cid The unique identifier for a call (<type>:<id>)
 * @param createdAt Date/time of creation
 * @param createdBy * @param custom Custom data for this object
 * @param id Call ID
 * @param ownCapabilities The capabilities of the current user
 * @param recordEgress * @param settings * @param team * @param type The type of call
 * @param updatedAt Date/time of the last update
 * @param endedAt */

data class CallResponse(

    @Json(name = "broadcast_egress")
    val broadcastEgress: kotlin.String,

    /* The unique identifier for a call (<type>:<id>) */
    @Json(name = "cid")
    val cid: kotlin.String,

    /* Date/time of creation */
    @Json(name = "created_at")
    val createdAt: java.time.OffsetDateTime,

    @Json(name = "created_by")
    val createdBy: UserResponse,

    /* Custom data for this object */
    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, kotlin.Any>,

    /* Call ID */
    @Json(name = "id")
    val id: kotlin.String,

    /* The capabilities of the current user */
    @Json(name = "own_capabilities")
    val ownCapabilities: kotlin.collections.List<kotlin.String>,

    @Json(name = "record_egress")
    val recordEgress: kotlin.String,

    @Json(name = "settings")
    val settings: CallSettingsResponse,

    @Json(name = "team")
    val team: kotlin.String,

    /* The type of call */
    @Json(name = "type")
    val type: kotlin.String,

    /* Date/time of the last update */
    @Json(name = "updated_at")
    val updatedAt: java.time.OffsetDateTime,

    @Json(name = "ended_at")
    val endedAt: java.time.OffsetDateTime? = null

)
