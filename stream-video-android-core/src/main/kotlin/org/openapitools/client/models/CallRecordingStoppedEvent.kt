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





import com.squareup.moshi.Json

/**
 * This event is sent when call recording has stopped
 *
 * @param callCid 
 * @param createdAt 
 * @param type The type of event: \"call.recording_stopped\" in this case
 */


data class CallRecordingStoppedEvent (

    @Json(name = "call_cid")
    val callCid: kotlin.String,

    @Json(name = "created_at")
    val createdAt: java.time.OffsetDateTime,

    /* The type of event: \"call.recording_stopped\" in this case */
    @Json(name = "type")
    val type: kotlin.String

): VideoEvent(), WSCallEvent{ 
    override fun getCallCID(): String {
        return callCid
    }

    override fun getEventType(): String {
        return type
    }
}


