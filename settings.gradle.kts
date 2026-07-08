
rootProject.name = "Yobidashi4"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            library("koin", "io.insert-koin", "koin-core").version("4.2.2")
            library("koinAnnotations", "io.insert-koin", "koin-annotations").version("2.3.1")
            library("koinKspCompiler", "io.insert-koin", "koin-ksp-compiler").version("2.3.1")
            library("kotlinSerialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.10.0")
            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm").version("1.10.2")
            library("slf4j", "org.slf4j", "slf4j-api").version("2.0.17")
            library("reload4j", "org.slf4j", "slf4j-reload4j").version("2.0.17")
            library("zxing", "com.google.zxing", "core").version("3.5.4")
            library("jcef", "me.friwi", "jcefmaven").version("146.0.10")
            library("jsoup", "org.jsoup", "jsoup").version("1.22.2")
            library("okio", "com.squareup.okio", "okio").version("3.17.0")
            version("compose", "1.11.1")
            version("kover", "0.9.8")
            version("kotlin", "2.4.0")
            version("ksp", "2.3.9")
        }

        create("testLibraries") {
            version("junit", "5.14.3")
        }
    }
}

include(":domain")
include(":infrastructure")
include(":presentation")

