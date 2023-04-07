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
 * @param userId 
 * @param grantPermissions 
 * @param revokePermissions 
 */


data class UpdateUserPermissionsRequest (

    @Json(name = "user_id")
    val userId: kotlin.String,

    @Json(name = "grant_permissions")
    val grantPermissions: kotlin.collections.List<kotlin.String>? = null,

    @Json(name = "revoke_permissions")
    val revokePermissions: kotlin.collections.List<kotlin.String>? = null

)


