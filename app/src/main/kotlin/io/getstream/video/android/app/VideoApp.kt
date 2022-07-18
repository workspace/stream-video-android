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

package io.getstream.video.android.app

import android.app.Application
import io.getstream.video.android.client.VideoClient
import io.getstream.video.android.logging.LoggingLevel
import io.getstream.video.android.token.TokenProvider
import stream.video.User

class VideoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: VideoApp

        lateinit var videoClient: VideoClient
            private set

        /**
         * Sets up and returns the [videoClient] required to connect to the API.
         */
        fun initializeClient(
            apiKey: String,
            tokenProvider: TokenProvider,
            user: User
        ): VideoClient {
            videoClient = VideoClient
                .Builder(
                    apiKey = apiKey,
                    appContext = instance,
                    user = user,
                    tokenProvider = tokenProvider
                )
                .loggingLevel(LoggingLevel.BODY)
                .build()

            return videoClient
        }
    }
}
