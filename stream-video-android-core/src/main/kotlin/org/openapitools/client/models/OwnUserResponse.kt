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

import org.openapitools.client.models.Device

import com.squareup.moshi.Json

/**
 * 
 *
 * @param createdAt 
 * @param custom 
 * @param devices 
 * @param id 
 * @param role 
 * @param updatedAt 
 * @param deletedAt 
 * @param image 
 * @param name 
 * @param teams 
 */


data class OwnUserResponse (

    @Json(name = "created_at")
    val createdAt: java.time.OffsetDateTime,

    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, kotlin.Any>,

    @Json(name = "devices")
    val devices: kotlin.collections.List<Device>,

    @Json(name = "id")
    val id: kotlin.String,

    @Json(name = "role")
    val role: kotlin.String,

    @Json(name = "updated_at")
    val updatedAt: java.time.OffsetDateTime,

    @Json(name = "deleted_at")
    val deletedAt: java.time.OffsetDateTime? = null,

    @Json(name = "image")
    val image: kotlin.String? = null,

    @Json(name = "name")
    val name: kotlin.String? = null,

    @Json(name = "teams")
    val teams: kotlin.collections.List<kotlin.String>? = null

)

