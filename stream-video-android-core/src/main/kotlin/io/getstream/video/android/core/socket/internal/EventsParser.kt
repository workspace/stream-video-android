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

package io.getstream.video.android.core.socket.internal

import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.video.android.core.errors.VideoErrorCode
import io.getstream.video.android.core.errors.create
import io.getstream.video.android.core.events.ConnectedEvent
import io.getstream.video.android.core.events.CoordinatorHealthCheckEvent
import io.getstream.video.android.core.socket.VideoSocket
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Response
import okhttp3.WebSocket

@Suppress("TooManyFunctions")
internal class EventsParser(
    private val videoSocket: VideoSocket,
) : okhttp3.WebSocketListener() {

    private val logger by taggedLogger("Call:WS-Events")

    private var connectionEventReceived = false
    private var closedByClient = true

    override fun onOpen(webSocket: WebSocket, response: Response) {
        connectionEventReceived = false
        closedByClient = false

        videoSocket.authenticateUser()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)

        try {
            val data = Json.decodeFromString<JsonObject>(text)
            logger.d { "[onMessage] $data" }

            val eventType = EventType.from(data["type"]?.jsonPrimitive?.content ?: return)
            val processedEvent = EventMapper.mapEvent(eventType, text)

            if (processedEvent !is CoordinatorHealthCheckEvent) logger.v { "[onMessage] processedEvent: $processedEvent" }

            if (!connectionEventReceived && processedEvent is CoordinatorHealthCheckEvent) {
                connectionEventReceived = true
                videoSocket.onConnectionResolved(ConnectedEvent(processedEvent.clientId))
            } else {
                videoSocket.onEvent(processedEvent)
            }
        } catch (error: Throwable) {
            logger.e(error) { "[onMessage] failed: $error" }
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        // no-op
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        if (code == CODE_CLOSE_SOCKET_FROM_CLIENT) {
            closedByClient = true
        } else {
            // Treat as failure and reconnect, socket shouldn't be closed by server
            onFailure(Error.NetworkError.create(VideoErrorCode.SOCKET_CLOSED))
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        // Called when socket is disconnected by client also (client.disconnect())
        onSocketError(Error.NetworkError.create(VideoErrorCode.SOCKET_FAILURE, t))
    }

    private fun onFailure(streamError: Error.NetworkError) {
        // Called when socket is disconnected by client also (client.disconnect())
        onSocketError(Error.NetworkError.create(VideoErrorCode.SOCKET_FAILURE, streamError.cause))
    }

    internal fun closeByClient() {
        closedByClient = true
    }

    private fun onSocketError(error: Error.NetworkError) {
        if (!closedByClient) {
            videoSocket.onSocketError(error)
        }
    }

    internal companion object {
        internal const val CODE_CLOSE_SOCKET_FROM_CLIENT = 1000
    }
}
