import io.getstream.video.android.Configuration

plugins {
  kotlin("jvm")
}

rootProject.extra.apply {
  set("PUBLISH_GROUP_ID", Configuration.artifactGroup)
  set("PUBLISH_ARTIFACT_ID", "stream-video-android-bom")
  set("PUBLISH_VERSION", rootProject.extra.get("rootVersionName"))
}

dependencies {
  constraints {
    api(project(":stream-video-android-core"))
    api(project(":stream-video-android-ui-common"))
    api(project(":stream-video-android-xml"))
    api(project(":stream-video-android-compose"))
  }
}

apply(from ="${rootDir}/scripts/publish-module.gradle")