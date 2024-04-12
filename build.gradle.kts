import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    id("org.jetbrains.compose") version "1.6.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
    // Apply the application plugin to add support for building a CLI application in Java.
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
    id("org.jetbrains.kotlinx.kover") version "0.7.6"
}

group = "jp.toastkid.yobidashi4"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(project(path = ":domain"))
    implementation(project(path = ":presentation"))
    implementation(project(path = ":infrastructure"))

    implementation("org.jetbrains.compose.runtime:runtime:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1")

    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.slf4j:slf4j-reload4j:2.0.6")

    implementation("io.insert-koin:koin-core:3.3.2")
    implementation("io.insert-koin:koin-annotations:1.0.3")
    ksp("io.insert-koin:koin-ksp-compiler:1.0.3")

    // Kover
    kover(project(path = ":domain"))
    kover(project(path = ":presentation"))
    kover(project(path = ":infrastructure"))
}

allprojects {
    repositories {
        google()
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
        kotlinOptions.jvmTarget = org.jetbrains.kotlin.config.JvmTarget.JVM_17.description
        kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=all")
    }

    tasks.test {
        maxParallelForks = Runtime.getRuntime().availableProcessors()
        useJUnitPlatform()
        jvmArgs(
            "--add-opens", "java.base/java.nio.file=ALL-UNNAMED",
            "--add-opens", "java.base/java.time=ALL-UNNAMED",
            "--add-opens", "java.base/java.time.chrono=ALL-UNNAMED",
            "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
            "--add-opens", "java.desktop/java.awt.dnd=ALL-UNNAMED"
        )
    }
}

koverReport {
    // common filters for all reports of all variants
    filters {
        // exclusions for reports
        excludes {
            // excludes class by fully-qualified JVM class name, wildcards '*' and '?' are available
            classes("jp.toastkid.yobidashi4.infrastructure.di.*")
            classes("*ComposableSingletons*")
            classes("*\$inject\$*")
            packages("org.koin.ksp.generated")
        }
    }
}

val libraryPath = "jcef-bundle/"

compose.desktop {
    //"-Djava.library.path=$libraryPath"
    application {
        mainClass = "jp.toastkid.yobidashi4.main.MainKt"

        nativeDistributions {
            packageVersion = rootProject.version.toString()
            description = "Yobidashi 4 is a Toast kid's super tool aop."
            copyright = "(c) 2022 toastkidjp. All rights reserved."
            vendor = "Toast kid"
            //licenseFile.set(project.file("LICENSE.txt"))
            //includeAllModules = true
            modules("java.instrument", "java.sql", "jdk.unsupported")
            outputBaseDir.set(project.rootDir.resolve("."))
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe
            )
            windows {
                iconFile.set(project.file("presentation/src/main/resources/images/icon.png"))
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

/*TODO
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}*/
