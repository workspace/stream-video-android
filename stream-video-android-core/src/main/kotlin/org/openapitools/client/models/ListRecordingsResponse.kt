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

import org.openapitools.client.models.CallRecording

import com.squareup.moshi.Json

/**
 * 
 *
 * @param duration 
 * @param recordings 
 */


data class ListRecordingsResponse (

    @Json(name = "duration")
    val duration: kotlin.String,

    @Json(name = "recordings")
    val recordings: kotlin.collections.List<CallRecording>

)

