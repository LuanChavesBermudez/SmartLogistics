// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

val localBuildRoot =
    file(System.getenv("LOCALAPPDATA")).resolve("SmartLogistics/build-normal")

subprojects {
    layout.buildDirectory.set(localBuildRoot.resolve(name))
}
