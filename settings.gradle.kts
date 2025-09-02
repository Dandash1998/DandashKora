pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // We add JitPack back just in case, but no credentials are needed.
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "DandashKora"
include(":app")

