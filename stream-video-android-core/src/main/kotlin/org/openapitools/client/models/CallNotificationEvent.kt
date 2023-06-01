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
import org.openapitools.client.models.UserResponse




import com.squareup.moshi.Json

/**
 * This event is sent to all call members to notify they are getting called
 *
 * @param call 
 * @param callCid 
 * @param createdAt 
 * @param members Call members
 * @param sessionId Call session ID
 * @param type The type of event: \"call.notification\" in this case
 * @param user 
 */


data class CallNotificationEvent (

    @Json(name = "call")
    val call: CallResponse,

    @Json(name = "call_cid")
    val callCid: kotlin.String,

    @Json(name = "created_at")
    val createdAt: org.threeten.bp.OffsetDateTime,

    /* Call members */
    @Json(name = "members")
    val members: kotlin.collections.List<MemberResponse>,

    /* Call session ID */
    @Json(name = "session_id")
    val sessionId: kotlin.String,

    /* The type of event: \"call.notification\" in this case */
    @Json(name = "type")
    val type: kotlin.String = "call.notification",

    @Json(name = "user")
    val user: UserResponse

) : VideoEvent(), WSCallEvent{ 

    override fun getCallCID(): String {
        return callCid
    }

    override fun getEventType(): String {
        return type
    }
}



