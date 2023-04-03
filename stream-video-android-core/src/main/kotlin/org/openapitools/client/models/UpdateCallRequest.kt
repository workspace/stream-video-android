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

import org.openapitools.client.models.CallSettingsRequest

import com.squareup.moshi.Json

/**
 * 
 *
 * @param custom Custom data for this object
 * @param settingsOverride 
 */


data class UpdateCallRequest (

    /* Custom data for this object */
    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, kotlin.Any>? = null,

    @Json(name = "settings_override")
    val settingsOverride: CallSettingsRequest? = null

)

