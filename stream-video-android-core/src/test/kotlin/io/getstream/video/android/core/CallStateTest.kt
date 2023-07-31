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

package io.getstream.video.android.core

import com.google.common.truth.Truth.assertThat
import io.getstream.video.android.core.base.IntegrationTestBase
import io.getstream.video.android.core.events.DominantSpeakerChangedEvent
import io.getstream.video.android.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.openapitools.client.models.CallSettingsRequest
import org.openapitools.client.models.MemberRequest
import org.openapitools.client.models.ScreensharingSettingsRequest
import org.robolectric.RobolectricTestRunner
import org.threeten.bp.Clock
import org.threeten.bp.OffsetDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CallStateTest : IntegrationTestBase() {

    /**
     * State should be populated if you
     * - Create a call
     * - Get a call
     * - Join a call
     * - Query a call
     */

    @Test
    fun `Creating a call should populate the state`() = runTest {
        val call = client.call("default", randomUUID())
        // test with custom field, members and a settings overwrite
        val custom = mapOf("foo" to "bar")
        val response = call.create(
            custom = custom,
            members = listOf(MemberRequest("tommaso", mutableMapOf("color" to "green"))),
            // block screensharing completely for this call
            settings = CallSettingsRequest(
                screensharing = ScreensharingSettingsRequest(
                    accessRequestEnabled = false,
                    enabled = false
                )
            )
        )
        assertSuccess(response)

        // verify we can't screenshare
        call.state.settings.value?.apply {
            assertThat(this).isNotNull()
            assertThat(screensharing.enabled).isFalse()
            assertThat(screensharing.accessRequestEnabled).isFalse()
        }
        assertThat(call.state.members.value.size).isEqualTo(1)
        val memberNames = call.state.members.value.map { it.user.id }
        assertThat(memberNames).containsExactly("tommaso")
        val tommasoMember = call.state.members.value.first { it.user.id == "tommaso" }
        assertThat(tommasoMember.custom["color"]).isEqualTo("green")

        assertThat(call.state.custom.value["foo"]).isEqualTo("bar")
    }

    @Test
    fun `Getting a call should populate the state`() = runTest {
        val response = call.get()
        assertSuccess(response)
        assertThat(call.state.settings.value).isNotNull()
    }

    @Test
    fun `Joining a call should populate the state`() = runTest {
        val call = client.call("default", randomUUID())
        val response = call.joinRequest(
            create = CreateCallOptions(custom = mapOf("color" to "green")),
            location = "AMS"
        )
        assertSuccess(response)
        assertThat(call.state.settings.value).isNotNull()
        assertThat(call.state.custom.value["color"]).isEqualTo("green")
    }

    /**
     * * anyone who is pinned
     * * dominant speaker
     * * if you are screensharing
     * * last speaking at
     * * all other video participants by when they joined
     * * audio only participants by when they joined
     */
    @Test
    fun `Participants should be sorted`() = runTest {
        val call = client.call("default", randomUUID())

        val sortedParticipants = call.state.sortedParticipantsFlow.stateIn(backgroundScope, SharingStarted.Eagerly, emptyList())

        val sorted1 = sortedParticipants.value
        assertThat(sorted1).isEmpty()

        call.state._pinnedParticipants.value = mutableMapOf(
            "1" to OffsetDateTime.now(Clock.systemUTC())
        )

        call.state.updateParticipant(
            ParticipantState("4", call, User("4")).apply { _lastSpeakingAt.value = nowUtc }
        )
        call.state.updateParticipant(
            ParticipantState("5", call, User("5")).apply { _videoEnabled.value = true }
        )
        call.state.updateParticipant(
            ParticipantState("6", call, User("6")).apply { _joinedAt.value = nowUtc }
        )

        call.state.updateParticipant(
            ParticipantState("1", call, User("1"))
        )
        call.state.updateParticipant(
            ParticipantState("2", call, User("2")).apply { _dominantSpeaker.value = true }
        )
        call.state.updateParticipant(
            ParticipantState("3", call, User("3")).apply { _screenSharingEnabled.value = true }
        )

        val participants = call.state.participants.value
        println("emitSorted participants size is ${participants.size}")
        assertThat(participants.size).isEqualTo(6)
        delay(60)

        val sorted2 = sortedParticipants.value.map { it.sessionId }
        assertThat(sorted2).isEqualTo(listOf("1", "2", "3", "4", "5", "6"))

        clientImpl.fireEvent(DominantSpeakerChangedEvent("3", "3"), call.cid)
        assertThat(call.state.getParticipantBySessionId("3")?.dominantSpeaker?.value).isTrue()
        delay(60)

        val sorted3 = sortedParticipants.value.map { it.sessionId }
        assertThat(sorted3).isEqualTo(listOf("1", "3", "2", "4", "5", "6"))
    }

    @Test
    fun `Querying calls should populate the state`() = runTest {
//        val createResult = client.call("default", randomUUID()).create(custom=mapOf("color" to "green"))
//        assertSuccess(createResult)
        val filters = mutableMapOf("color" to "green")
        val queryResult = client.queryCalls(filters, limit = 1)
        assertSuccess(queryResult)
        // verify the call has settings setup correctly
        queryResult.onSuccess {
            assertThat(it.calls.size).isGreaterThan(0)
            it.calls.forEach {
                val call = clientImpl.call(it.call.type, it.call.id)
                assertThat(call.state.settings.value).isNotNull()
            }
        }
    }

    @Test
    fun `Setting the speaking while muted flag will reset itself after delay`() = runTest {
        // we can make multiple calls, this should have no impact on the reset logic or duration
        val speakingWhileMuted = call.state.speakingWhileMuted
        call.state.markSpeakingAsMuted()
        call.state.markSpeakingAsMuted()
        call.state.markSpeakingAsMuted()

        assertTrue(call.state.speakingWhileMuted.first())
        // The flag should automatically reset to false 2 seconds
        advanceTimeBy(3000)

        assertFalse(call.state.speakingWhileMuted.first())
    }
}
