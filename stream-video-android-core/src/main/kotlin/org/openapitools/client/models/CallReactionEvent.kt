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

import org.openapitools.client.models.ReactionResponse




import com.squareup.moshi.Json

/**
 * This event is sent when a reaction is sent in a call, clients should use this to show the reaction in the call screen
 *
 * @param callCid 
 * @param createdAt 
 * @param reaction 
 * @param type The type of event: \"call.reaction_new\" in this case
 */


data class CallReactionEvent (

    @Json(name = "call_cid")
    val callCid: kotlin.String,

    @Json(name = "created_at")
    val createdAt: java.time.OffsetDateTime,

    @Json(name = "reaction")
    val reaction: ReactionResponse,

    /* The type of event: \"call.reaction_new\" in this case */
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


