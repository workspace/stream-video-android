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

package org.openapitools.client.apis


import retrofit2.http.Body
import retrofit2.http.POST






import org.openapitools.client.models.CreateDeviceRequest
import org.openapitools.client.models.Response

interface PushApi {
    /**
     * Create device
     * Adds a new device to a user, if the same device already exists the call will have no effect
     * Responses:
     *  - 201: Successful response
     *  - 400: Bad request
     *  - 429: Too many requests
     *
     * @param createDeviceRequest
     * @return [Response]
     */
    @POST("/video/devices")
    suspend fun createDevice(
        @Body createDeviceRequest: CreateDeviceRequest
    ): Response

}
