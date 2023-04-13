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
 * This event is sent when a call is mark as ended for all its participants. Clients receiving this event should leave the call screen
 *
 * @param callCid 
 * @param createdAt 
 * @param type The type of event: \"call.ended\" in this case
 * @param user 
 */


data class CallEndedEvent (

    @Json(name = "call_cid")
    val callCid: kotlin.String,

    @Json(name = "created_at")
    val createdAt: java.time.OffsetDateTime,

    /* The type of event: \"call.ended\" in this case */
    @Json(name = "type")
    val type: kotlin.String,

    @Json(name = "user")
    val user: UserResponse? = null

): VideoEvent(), WSCallEvent{ 
    override fun getCallCID(): String {
        return callCid
    }

    override fun getEventType(): String {
        return type
    }
}


