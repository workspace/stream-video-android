/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
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

package io.getstream.video.android.model

import stream.video.sfu.models.Participant

public data class CallParticipantState(
    public val id: String,
    public val role: String,
    public val name: String,
    public val profileImageURL: String?,
    public val isLocal: Boolean,
    public var isOnline: Boolean,
    public var hasVideo: Boolean,
    public var hasAudio: Boolean,
    public var track: VideoTrack?,
    public var trackSize: Pair<Int, Int>,
    public var audioLevel: Float,
    public val idPrefix: String
)

public fun Participant.toCallParticipant(currentUserId: String): CallParticipantState =
    CallParticipantState(
        id = this.user?.id ?: "",
        name = this.user?.name ?: "",
        role = this.user?.role ?: "",
        profileImageURL = this.user?.image_url,
        isLocal = currentUserId == this.user?.id,
        isOnline = true,
        hasVideo = video,
        hasAudio = audio,
        track = null,
        trackSize = 0 to 0,
        audioLevel = 0f,
        idPrefix = track_lookup_prefix
    )

public fun CallParticipantState.toUser(): User {
    return User(
        id = id,
        role = role,
        name = name,
        token = "",
        imageUrl = profileImageURL,
        teams = emptyList(),
        extraData = emptyMap()
    )
}
