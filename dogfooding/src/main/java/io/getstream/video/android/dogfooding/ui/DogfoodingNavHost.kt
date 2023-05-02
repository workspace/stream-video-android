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

package io.getstream.video.android.dogfooding.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.getstream.video.android.dogfooding.ui.login.LoginScreen

@Composable
fun DogfoodingNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = DogfoodingScreens.Login.destination
) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination
    ) {
        composable(DogfoodingScreens.Login.destination) {
            LoginScreen(
                navigateToCallJoin = {
                    navController.navigate(DogfoodingScreens.CallJoin.destination)
                }
            )
        }
        composable(DogfoodingScreens.CallJoin.destination) {
        }
        composable(DogfoodingScreens.CallPreview.destination) {
        }
    }
}

enum class DogfoodingScreens(val destination: String) {
    Login("login"),
    CallJoin("call_join"),
    CallPreview("call_preview");
}
