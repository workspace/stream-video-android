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

import com.squareup.moshi.Json

/**
 * 
 *
 * @param call 
 * @param duration Duration of the request in human-readable format
 */


data class StopLiveResponse (

    @Json(name = "call")
    val call: CallResponse,

    /* Duration of the request in human-readable format */
    @Json(name = "duration")
    val duration: kotlin.String

)

