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
}

dependencies {
    implementation(project(path = ":domain"))
    implementation(project(path = ":presentation"))

    implementation(compose.desktop.currentOs)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    implementation("com.fifesoft:rsyntaxtextarea:3.3.2")
    implementation("me.friwi:jcefmaven:110.0.25")

    implementation("io.insert-koin:koin-core:3.3.2")
    implementation("io.insert-koin:koin-annotations:1.0.3")
    ksp("io.insert-koin:koin-ksp-compiler:1.0.3")

    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.slf4j:slf4j-reload4j:2.0.6")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("io.mockk:mockk:1.10.6")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

// KSP - To use generated sources
sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}