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

import org.openapitools.client.models.CallRequest




import com.squareup.moshi.Json

/**
 * 
 *
 * @param `data` 
 * @param membersLimit 
 * @param ring if provided it overrides the default ring setting for this call
 */


data class GetOrCreateCallRequest (

    @Json(name = "data")
    val `data`: CallRequest? = null,

    @Json(name = "members_limit")
    val membersLimit: kotlin.Int? = null,

    /* if provided it overrides the default ring setting for this call */
    @Json(name = "ring")
    val ring: kotlin.Boolean? = null

)




