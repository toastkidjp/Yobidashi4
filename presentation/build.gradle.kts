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

    implementation(libraries.slf4j)
    implementation(libraries.reload4j)

    implementation(libraries.koin)
    implementation(libraries.koinAnnotations)
    ksp(libraries.koinKspCompiler)

    implementation("com.github.skydoves:colorpicker-compose:1.1.2")
    testImplementation(compose.desktop.uiTestJUnit4)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "jp.toastkid.yobidashi4.library.resources"
    generateResClass = auto
}
