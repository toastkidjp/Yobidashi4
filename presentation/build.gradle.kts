plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlinx.kover")
}

group = "jp.toastkid.yobidashi4"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(project(path = ":domain"))

    implementation("me.friwi:jcefmaven:110.0.25")

    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.slf4j:slf4j-reload4j:2.0.6")

    implementation("io.insert-koin:koin-core:3.3.2")
    implementation("io.insert-koin:koin-annotations:1.0.3")
    ksp("io.insert-koin:koin-ksp-compiler:1.0.3")

    implementation("com.halilibo.compose-richtext:richtext-commonmark:0.16.0")
    implementation("com.halilibo.compose-richtext:richtext-ui-material:0.16.0")
    implementation("com.godaddy.android.colorpicker:compose-color-picker:0.4.2")
}
