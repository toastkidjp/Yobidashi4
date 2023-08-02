plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlinx.kover")
}

group = "jp.toastkid.yobidashi4"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
}
