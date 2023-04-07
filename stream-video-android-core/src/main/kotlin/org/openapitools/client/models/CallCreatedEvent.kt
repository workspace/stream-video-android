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

import org.openapitools.client.models.CallResponse
import org.openapitools.client.models.MemberResponse




import com.squareup.moshi.Json

/**
 * This event is sent when a call is created. Clients receiving this event should check if the ringing  field is set to true and if so, show the call screen
 *
 * @param call 
 * @param callCid 
 * @param createdAt 
 * @param members the members added to this call
 * @param ringing true when the call was created with ring enabled
 * @param type The type of event: \"call.created\" in this case
 */


data class CallCreatedEvent (

    @Json(name = "call")
    val call: CallResponse,

    @Json(name = "call_cid")
    val callCid: kotlin.String,

    @Json(name = "created_at")
    val createdAt: java.time.OffsetDateTime,

    /* the members added to this call */
    @Json(name = "members")
    val members: kotlin.collections.List<MemberResponse>,

    /* true when the call was created with ring enabled */
    @Json(name = "ringing")
    val ringing: kotlin.Boolean,

    /* The type of event: \"call.created\" in this case */
    @Json(name = "type")
    val type: kotlin.String

) : WSEvent(), WSCallEvent{
    override fun getCallCID(): String {
        return callCid
    }
}



