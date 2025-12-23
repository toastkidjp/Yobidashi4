
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
            library("koin", "io.insert-koin", "koin-core").version("4.1.1")
            library("koinAnnotations", "io.insert-koin", "koin-annotations").version("2.3.1")
            library("koinKspCompiler", "io.insert-koin", "koin-ksp-compiler").version("2.3.1")
            library("kotlinSerialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.9.0")
            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm").version("1.10.2")
            library("slf4j", "org.slf4j", "slf4j-api").version("2.0.17")
            library("reload4j", "org.slf4j", "slf4j-reload4j").version("2.0.17")
            library("zxing", "com.google.zxing", "core").version("3.5.4")
            library("jcef", "me.friwi", "jcefmaven").version("141.0.10")
            library("jsoup", "org.jsoup", "jsoup").version("1.21.2")
            version("compose", "1.8.0-beta02")
            version("kover", "0.9.4")
            version("kotlin", "2.2.10")
            version("ksp", "2.2.10-2.0.2")
        }

        create("testLibraries") {
            version("junit", "5.14.1")
        }
    }
}

include(":domain")
include(":infrastructure")
include(":presentation")

