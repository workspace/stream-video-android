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
 * @param id User ID
 * @param custom 
 * @param image 
 * @param name Optional name of user
 * @param role 
 * @param teams 
 */


data class UserRequest (

    /* User ID */
    @Json(name = "id")
    val id: kotlin.String,

    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, kotlin.Any>? = null,

    @Json(name = "image")
    val image: kotlin.String? = null,

    /* Optional name of user */
    @Json(name = "name")
    val name: kotlin.String? = null,

    @Json(name = "role")
    val role: kotlin.String? = null,

    @Json(name = "teams")
    val teams: kotlin.collections.List<kotlin.String>? = null

)

