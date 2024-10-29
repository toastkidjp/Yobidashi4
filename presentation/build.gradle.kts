plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
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
    implementation(compose.components.resources)

    implementation(project(path = ":domain"))

    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.slf4j:slf4j-reload4j:2.0.6")

    implementation("io.insert-koin:koin-core:4.0.0")
    implementation("io.insert-koin:koin-annotations:1.4.0")
    ksp("io.insert-koin:koin-ksp-compiler:1.4.0")

    implementation("com.godaddy.android.colorpicker:compose-color-picker:0.4.2")
    testImplementation(compose.desktop.uiTestJUnit4)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "jp.toastkid.yobidashi4.library.resources"
    generateResClass = auto
}
