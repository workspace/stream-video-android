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

import org.openapitools.client.models.CallStateResponseFields

import com.squareup.moshi.Json

/**
 * 
 *
 * @param calls 
 * @param duration 
 * @param next 
 * @param prev 
 */


data class QueryCallsResponse (

    @Json(name = "calls")
    val calls: kotlin.collections.List<CallStateResponseFields>,

    @Json(name = "duration")
    val duration: kotlin.String,

    @Json(name = "next")
    val next: kotlin.String? = null,

    @Json(name = "prev")
    val prev: kotlin.String? = null

)

