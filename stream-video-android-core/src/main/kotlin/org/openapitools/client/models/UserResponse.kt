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
 * @param custom 
 * @param id 
 * @param role 
 * @param teams 
 * @param updatedAt Date/time of the last update
 * @param deletedAt Date/time of deletion
 * @param image 
 * @param name 
 */


data class UserResponse (

    /* Date/time of creation */
    @Json(name = "created_at")
    val createdAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, kotlin.Any>,

    @Json(name = "id")
    val id: kotlin.String,

    @Json(name = "role")
    val role: kotlin.String,

    @Json(name = "teams")
    val teams: kotlin.collections.List<kotlin.String>,

    /* Date/time of the last update */
    @Json(name = "updated_at")
    val updatedAt: org.threeten.bp.OffsetDateTime,

    /* Date/time of deletion */
    @Json(name = "deleted_at")
    val deletedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "image")
    val image: kotlin.String? = null,

    @Json(name = "name")
    val name: kotlin.String? = null

)




