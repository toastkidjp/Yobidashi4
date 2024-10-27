import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.20"
    id("org.jetbrains.compose") version "1.7.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
    // Apply the application plugin to add support for building a CLI application in Java.
    id("com.google.devtools.ksp") version "2.0.20-1.0.25"
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
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

    implementation("io.insert-koin:koin-core:4.0.0")
    implementation("io.insert-koin:koin-annotations:1.4.0")
    ksp("io.insert-koin:koin-ksp-compiler:1.4.0")

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
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
        testImplementation("io.mockk:mockk:1.10.6")
        testRuntimeOnly("net.bytebuddy:byte-buddy:1.12.22")
    }

    tasks.withType<KotlinCompile>() {
        kotlin {
            compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
            compilerOptions.freeCompilerArgs = listOf("-Xjvm-default=all", "-opt-in=kotlin.RequiresOptIn")
        }
    }

    tasks.test {
        maxParallelForks = Runtime.getRuntime().availableProcessors()
        maxHeapSize = "4G"
        useJUnitPlatform()
        jvmArgs(
            "--add-opens", "java.base/java.nio.file=ALL-UNNAMED",
            "--add-opens", "java.base/java.net=ALL-UNNAMED",
            "--add-opens", "java.base/java.time=ALL-UNNAMED",
            "--add-opens", "java.base/java.time.chrono=ALL-UNNAMED",
            "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
            "--add-opens", "java.desktop/java.awt.dnd=ALL-UNNAMED"
        )
    }
}

kover {
    reports {
        total {
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
                joinOutputJars.set(true)
            }
        }
    }
}

/*TODO
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}*/
