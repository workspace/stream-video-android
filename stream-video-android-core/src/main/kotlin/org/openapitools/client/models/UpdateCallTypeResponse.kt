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

import com.squareup.moshi.Json

/**
 * 
 *
 * @param createdAt 
 * @param duration 
 * @param grants 
 * @param name 
 * @param settings 
 * @param updatedAt 
 */


data class UpdateCallTypeResponse (

    @Json(name = "created_at")
    val createdAt: java.time.OffsetDateTime,

    @Json(name = "duration")
    val duration: kotlin.String,

    @Json(name = "grants")
    val grants: kotlin.collections.Map<kotlin.String, kotlin.collections.List<kotlin.String>>,

    @Json(name = "name")
    val name: kotlin.String,

    @Json(name = "settings")
    val settings: CallSettingsResponse,

    @Json(name = "updated_at")
    val updatedAt: java.time.OffsetDateTime

)

