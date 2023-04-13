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

import org.openapitools.client.models.BlockedUserEvent
import org.openapitools.client.models.CallAcceptedEvent
import org.openapitools.client.models.CallCreatedEvent
import org.openapitools.client.models.CallEndedEvent
import org.openapitools.client.models.CallMemberAddedEvent
import org.openapitools.client.models.CallMemberRemovedEvent
import org.openapitools.client.models.CallMemberUpdatedEvent
import org.openapitools.client.models.CallMemberUpdatedPermissionEvent
import org.openapitools.client.models.CallReactionEvent
import org.openapitools.client.models.CallRecordingStartedEvent
import org.openapitools.client.models.CallRecordingStoppedEvent
import org.openapitools.client.models.CallRejectedEvent
import org.openapitools.client.models.CallResponse
import org.openapitools.client.models.CallUpdatedEvent
import org.openapitools.client.models.CustomVideoEvent
import org.openapitools.client.models.HealthCheckEvent
import org.openapitools.client.models.MemberResponse
import org.openapitools.client.models.OwnCapability
import org.openapitools.client.models.OwnUserResponse
import org.openapitools.client.models.PermissionRequestEvent
import org.openapitools.client.models.ReactionResponse
import org.openapitools.client.models.UnblockedUserEvent
import org.openapitools.client.models.UpdatedCallPermissionsEvent
import org.openapitools.client.models.UserResponse
import org.openapitools.client.models.WSConnectedEvent




import com.squareup.moshi.*
import org.json.JSONObject

/**
 * The discriminator object for all websocket events, you should use this to map event payloads to their own type
 *
 */


public sealed class WSEvent {
}

class WSEventAdapter : JsonAdapter<WSEvent>() {
    private val moshi = Moshi.Builder().build()

    override fun fromJson(reader: JsonReader): WSEvent? {
        val jsonObject = reader.readJsonValue() as? JSONObject ?: return null
        val type = jsonObject.optString("type", null) ?: return null
        val jsonAdapter = moshi.adapter(getSubclass(type))
        return jsonAdapter.fromJson(jsonObject.toString())
    }

    override fun toJson(writer: JsonWriter, value: WSEvent?) {
        throw UnsupportedOperationException("toJson not implemented")
    }

    private fun getSubclass(type: String): Class<out WSEvent> {
        return when (type) {
            "call.accepted" -> CallAcceptedEvent::class.java
            "call.blocked_user" -> BlockedUserEvent::class.java
            "call.created" -> CallCreatedEvent::class.java
            "call.ended" -> CallEndedEvent::class.java
            "call.member_added" -> CallMemberAddedEvent::class.java
            "call.member_removed" -> CallMemberRemovedEvent::class.java
            "call.member_updated" -> CallMemberUpdatedEvent::class.java
            "call.permission_request" -> PermissionRequestEvent::class.java
            "call.permissions_updated" -> UpdatedCallPermissionsEvent::class.java
            "call.reaction_new" -> CallReactionEvent::class.java
            "call.recording_started" -> CallRecordingStartedEvent::class.java
            "call.recording_stopped" -> CallRecordingStoppedEvent::class.java
            "call.rejected" -> CallRejectedEvent::class.java
            "call.unblocked_user" -> UnblockedUserEvent::class.java
            "call.updated" -> CallUpdatedEvent::class.java
            "call.updated_permission" -> CallMemberUpdatedPermissionEvent::class.java
            "connection.ok" -> WSConnectedEvent::class.java
            "custom" -> CustomVideoEvent::class.java
            "health.check" -> HealthCheckEvent::class.java
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}


