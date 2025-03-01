/*
 * Copyright (c) 2014-2023 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapitools.client.models

import org.openapitools.client.models.CallResponse
import org.openapitools.client.models.MemberResponse




import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import org.openapitools.client.infrastructure.Serializer

/**
 * This event is sent when one or more members get its role updated
 *
 * @param call
 * @param callCid
 * @param capabilitiesByRole The capabilities by role for this call
 * @param createdAt
 * @param members The list of members that were updated
 * @param type The type of event: \"call.member_added\" in this case
 */


data class CallMemberUpdatedPermissionEvent (

    @Json(name = "call")
    val call: CallResponse,

    @Json(name = "call_cid")
    val callCid: kotlin.String,

    /* The capabilities by role for this call */
    @Json(name = "capabilities_by_role")
    val capabilitiesByRole: kotlin.collections.Map<kotlin.String, kotlin.collections.List<kotlin.String>>,

    @Json(name = "created_at")
    val createdAt: org.threeten.bp.OffsetDateTime,

    /* The list of members that were updated */
    @Json(name = "members")
    val members: kotlin.collections.List<MemberResponse>,

    /* The type of event: \"call.member_added\" in this case */
    @Json(name = "type")
    val type: kotlin.String = "call.member_updated_permission"

) : VideoEvent(), WSCallEvent {

    override fun getCallCID(): String {
        return callCid
    }

    override fun getEventType(): String {
        return type
    }
}
