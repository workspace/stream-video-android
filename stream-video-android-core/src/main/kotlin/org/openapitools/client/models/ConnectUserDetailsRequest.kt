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
 * @param id 
 * @param custom 
 * @param image 
 * @param name 
 */


data class ConnectUserDetailsRequest (

    @Json(name = "id")
    val id: kotlin.String,

    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, kotlin.Any>? = null,

    @Json(name = "image")
    val image: kotlin.String? = null,

    @Json(name = "name")
    val name: kotlin.String? = null

)


