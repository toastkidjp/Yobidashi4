import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.compose") version "1.2.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
    // Apply the application plugin to add support for building a CLI application in Java.
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

group = "jp.toastkid.yobidashi4"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

dependencies {
    implementation(project(path = ":domain"))
    implementation(project(path = ":presentation"))
    implementation(project(path = ":infrastructure"))

    implementation(compose.desktop.currentOs)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.slf4j:slf4j-reload4j:2.0.6")

    implementation("io.insert-koin:koin-core:3.3.2")
    implementation("io.insert-koin:koin-annotations:1.0.3")
    ksp("io.insert-koin:koin-ksp-compiler:1.0.3")
}

compose.desktop {
    //"-Djava.library.path=$libraryPath"
    application {
        mainClass = "jp.toastkid.yobidashi4.main.MainKt"
        javaHome = System.getenv("JDK_16")

        nativeDistributions {
            packageVersion = rootProject.version.toString()
            description = "Yobidashi 4 is a Toast kid's super tool aop."
            copyright = "c2022 toastkidjp. All rights reserved."
            vendor = "Toast kid"
            //licenseFile.set(project.file("LICENSE.txt"))
            includeAllModules = true
            outputBaseDir.set(project.rootDir.resolve("."))
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            windows {
                iconFile.set(project.file("src/main/resources/images/icon.png"))
            }
        }

        buildTypes.release {
            proguard {
                configurationFiles.from(project.file("compose-desktop.pro"))
                obfuscate.set(true)
            }
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "kotlin")

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
        testImplementation("io.mockk:mockk:1.10.6")
        testRuntimeOnly("net.bytebuddy:byte-buddy:1.12.22")
    }

    tasks.withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.test {
        useJUnitPlatform()
    }
}

val libraryPath = "jcef-bundle/"

val hostOs = System.getProperty("os.name")
open val target = when {
    hostOs == "Mac OS X" -> "macos"
    hostOs == "Linux" -> "linux"
    hostOs.startsWith("Win") -> "windows"
    else -> throw Error("Unknown os $hostOs")
}
/*TODO
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}*/
