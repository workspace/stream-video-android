/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapitools.client.models

import org.openapitools.client.models.CallSettingsResponse
import org.openapitools.client.models.OwnCapability
import org.openapitools.client.models.UserResponse




import com.squareup.moshi.Json

/**
 * Represents a call
 *
 * @param backstage 
 * @param blockedUserIds 
 * @param broadcasting 
 * @param cid The unique identifier for a call (<type>:<id>)
 * @param createdAt Date/time of creation
 * @param createdBy 
 * @param custom Custom data for this object
 * @param id Call ID
 * @param ownCapabilities The capabilities of the current user
 * @param recording 
 * @param settings 
 * @param transcribing 
 * @param type The type of call
 * @param updatedAt Date/time of the last update
 * @param endedAt Date/time when the call ended
 * @param startsAt Date/time when the call will start
 * @param team 
 */


data class CallResponse (

    @Json(name = "backstage")
    val backstage: kotlin.Boolean,

    @Json(name = "blocked_user_ids")
    val blockedUserIds: kotlin.collections.List<kotlin.String>,

    @Json(name = "broadcasting")
    val broadcasting: kotlin.Boolean,

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
    val ownCapabilities: kotlin.collections.List<OwnCapability>,

    @Json(name = "recording")
    val recording: kotlin.Boolean,

    @Json(name = "settings")
    val settings: CallSettingsResponse,

    @Json(name = "transcribing")
    val transcribing: kotlin.Boolean,

    /* The type of call */
    @Json(name = "type")
    val type: kotlin.String,

    /* Date/time of the last update */
    @Json(name = "updated_at")
    val updatedAt: java.time.OffsetDateTime,

    /* Date/time when the call ended */
    @Json(name = "ended_at")
    val endedAt: java.time.OffsetDateTime? = null,

    /* Date/time when the call will start */
    @Json(name = "starts_at")
    val startsAt: java.time.OffsetDateTime? = null,

    @Json(name = "team")
    val team: kotlin.String? = null

)


