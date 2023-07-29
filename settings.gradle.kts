
rootProject.name = "Yobidashi4"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":domain")
include(":infrastructure")
include(":presentation")

