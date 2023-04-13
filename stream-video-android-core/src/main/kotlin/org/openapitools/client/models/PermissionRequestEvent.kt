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

import org.openapitools.client.models.UserResponse




import com.squareup.moshi.Json

/**
 * This event is sent when a user requests access to a feature on a call, clients receiving this event should display a permission request to the user
 *
 * @param callCid 
 * @param createdAt 
 * @param permissions The list of permissions requested by the user
 * @param type The type of event: \"call.permission_request\" in this case
 * @param user 
 */


data class PermissionRequestEvent (

    @Json(name = "call_cid")
    val callCid: kotlin.String,

    @Json(name = "created_at")
    val createdAt: java.time.OffsetDateTime,

    /* The list of permissions requested by the user */
    @Json(name = "permissions")
    val permissions: kotlin.collections.List<kotlin.String>,

    /* The type of event: \"call.permission_request\" in this case */
    @Json(name = "type")
    val type: kotlin.String,

    @Json(name = "user")
    val user: UserResponse

): VideoEvent(), WSCallEvent{ 
    override fun getCallCID(): String {
        return callCid
    }

    override fun getEventType(): String {
        return type
    }
}


