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
 * 
 *
 * @param createdAt Date/time of creation
 * @param id 
 * @param pushProvider 
 * @param disabled Whether device is disabled or not
 * @param disabledReason Reason explaining why device had been disabled
 * @param pushProviderName 
 */


data class Device (

    /* Date/time of creation */
    @Json(name = "created_at")
    val createdAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "id")
    val id: kotlin.String,

    @Json(name = "push_provider")
    val pushProvider: kotlin.String,

    /* Whether device is disabled or not */
    @Json(name = "disabled")
    val disabled: kotlin.Boolean? = null,

    /* Reason explaining why device had been disabled */
    @Json(name = "disabled_reason")
    val disabledReason: kotlin.String? = null,

    @Json(name = "push_provider_name")
    val pushProviderName: kotlin.String? = null

)




