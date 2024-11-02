
rootProject.name = "Yobidashi4"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            library("koin", "io.insert-koin", "koin-core").version("4.0.0")
            library("koinAnnotations", "io.insert-koin", "koin-annotations").version("1.4.0")
            library("koinKspCompiler", "io.insert-koin", "koin-ksp-compiler").version("1.4.0")
        }
    }
}

include(":domain")
include(":infrastructure")
include(":presentation")

