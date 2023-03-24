import io.getstream.video.android.Configuration

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("io.getstream.android.library")
    id("io.getstream.video.generateServices")
    id("io.getstream.spotless")
    id(libs.plugins.kotlin.serialization.get().pluginId)
    id(libs.plugins.wire.get().pluginId)
}

rootProject.extra.apply {
    set("PUBLISH_GROUP_ID", Configuration.artifactGroup)
    set("PUBLISH_ARTIFACT_ID", "stream-video-android")
    set("PUBLISH_VERSION", rootProject.extra.get("rootVersionName"))
}

apply(from = "${rootDir}/scripts/publish-module.gradle")

wire {
    kotlin {
        rpcRole = "none"
    }

    protoPath {
        srcDir("src/main/proto")
    }
}

generateRPCServices {}

android {
    compileSdk = Configuration.compileSdk

    defaultConfig {
        minSdk = Configuration.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-proguard-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
            consumerProguardFiles("consumer-proguard-rules.pro")
        }

        debug {
            isMinifyEnabled = true
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
            consumerProguardFiles("consumer-proguard-rules.pro")
        }
    }

    resourcePrefix = "stream_video_"

    sourceSets.configureEach {
        kotlin.srcDir("build/generated/source/services")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    api(libs.stream.webrtc)
    api(libs.stream.webrtc.ui)

    implementation(libs.androidx.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    // lifecycle
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.viewmodel)

    // coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // API & Protobuf
    api(libs.wire.runtime)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.retrofit.scalars)
    implementation(libs.retrofit.wire.converter)
    implementation(libs.okhttp.logging)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)

    // Stream
    implementation(libs.stream.log)
    implementation(libs.stream.push)
    implementation(libs.stream.push.delegate)

    // unit test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}