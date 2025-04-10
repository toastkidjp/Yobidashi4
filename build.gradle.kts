
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version libraries.versions.kotlin
    id("org.jetbrains.compose") version libraries.versions.compose
    id("org.jetbrains.kotlin.plugin.compose") version libraries.versions.kotlin
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20"
    // Apply the application plugin to add support for building a CLI application in Java.
    id("com.google.devtools.ksp") version "2.1.20-1.0.31"
    id("org.jetbrains.kotlinx.kover") version libraries.versions.kover
}

group = "jp.toastkid.yobidashi4"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(project(path = ":domain"))
    implementation(project(path = ":presentation"))
    implementation(project(path = ":infrastructure"))

    implementation("org.jetbrains.compose.runtime:runtime:${libraries.versions.compose.get()}")
    implementation(libraries.kotlinSerialization)
    implementation(libraries.coroutines)

    implementation(libraries.slf4j)
    implementation(libraries.reload4j)

    implementation(libraries.koin)

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
        testRuntimeOnly("net.bytebuddy:byte-buddy:1.15.7")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(arrayOf(
            "--add-exports", "jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
            "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED"))
    }

    tasks.withType<KotlinCompile>() {

        kotlin {
            compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
            compilerOptions.freeCompilerArgs = listOf(
                "-Xjvm-default=all",
                "-opt-in=kotlin.RequiresOptIn",
                "-Xadd-modules=jdk.incubator.vector"
            )
        }
    }

    tasks.test {
        maxParallelForks = Runtime.getRuntime().availableProcessors()
        maxHeapSize = "8G"
        minHeapSize = "2G"
        useJUnitPlatform()
        jvmArgs(
            "--add-opens", "java.base/java.nio.file=ALL-UNNAMED",
            "--add-opens", "java.base/java.net=ALL-UNNAMED",
            "--add-opens", "java.base/java.time=ALL-UNNAMED",
            "--add-opens", "java.base/java.time.chrono=ALL-UNNAMED",
            "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
            "--add-opens", "java.desktop/java.awt=ALL-UNNAMED",
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
                    packages("jp.toastkid.yobidashi4.library.resources")
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
            copyright = "(c) 2025 toastkidjp. All rights reserved."
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
                version.set("7.4.0")
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

fun readCoverages(): MutableMap<String, String> {
    var started = false
    val keys = arrayOf(
        "Class",
        "Method",
        "Branch",
        "Line",
        "Instruction"
    );
    val map = mutableMapOf<String, String>()
    val buffer = StringBuffer()

    val file = File("build/reports/kover/html/index.html")
    if (file.exists().not()) {
        return map
    }

    val lines = file.readText().split("\n")
    for (i in (0 until lines.size)) {
        val line = lines[i]
        if (line.contains("<td class=\"name\">all classes</td>")) {
            started = true
        }
        if (!started) {
            continue
        }
        if (line.contains("<span class=\"percent\">")) {
            buffer.append(lines[i + 1].trim())
            continue
        }
        if (line.contains("<span class=\"absValue\">")) {
            buffer.append(" ").append(lines[i + 1].trim())
            map.put("${keys.get(map.size)}", buffer.toString())
            buffer.setLength(0);
            continue
        }
        if (line.contains("</table>")) {
            break
        }
    }
    return map
}

tasks.register("printCoverageSummary") {
    val map = readCoverages()

    doLast {
        println("| Category | Coverage(%)\n|:---|:---")
        map.map { "| ${it.key} | ${it.value}" }.forEach(::println)
    }
}
