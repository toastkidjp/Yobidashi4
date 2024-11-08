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

dependencies {
    implementation(project(path = ":domain"))
    implementation(project(path = ":presentation"))

    implementation(compose.desktop.currentOs)

    implementation(libraries.kotlinSerialization)

    implementation("me.friwi:jcefmaven:127.3.1")

    implementation(libraries.koin)
    implementation(libraries.koinAnnotations)
    ksp(libraries.koinKspCompiler)

    implementation("org.jsoup:jsoup:1.15.3")
    implementation(libraries.slf4j)
    implementation(libraries.reload4j)

    implementation("com.google.zxing:core:3.4.1")

    implementation("org.apache.lucene:lucene-core:9.11.0")
    implementation("org.apache.lucene:lucene-queryparser:9.11.0") {
        exclude("org.apache.lucene", "lucene-sandbox")
    }
}

// KSP - To use generated sources
sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}