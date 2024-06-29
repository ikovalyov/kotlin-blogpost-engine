import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import java.util.*

plugins {
    kotlin("multiplatform") version "2.0.0"
    kotlin("kapt") version "2.0.0"
    kotlin("plugin.allopen") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.diffplug.spotless") version "6.25.0"
    id("idea")
}

group = "com.github.ikovalyov"
version = "0.0.1"

repositories {
    mavenCentral()

    maven {
        url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    }
    maven {
        url = uri("https://kotlin.bintray.com/kotlinx")
    }
}

kotlin {
    /* Targets configuration omitted.
     *  To find out how to configure the targets, please follow the link:
     *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    jvm {
        withJava()
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
        }

        compilations.all {
            tasks.named<Test>("${target.name}Test") {
                useJUnitPlatform()
                testLogging {
                    events("passed", "skipped", "failed")
                    showStackTraces = true
                    exceptionFormat = TestExceptionFormat.FULL
//                    showStandardStreams = true
                }
            }
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.github.microutils:kotlin-logging:3.0.5")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
                implementation("com.benasher44:uuid:0.8.4")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(
                    project.dependencies.enforcedPlatform(
                        "org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.765",
                    ),
                )
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-tanstack-react-table")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom")
                implementation(npm("react", "18.2.0"))
                implementation(npm("react-dom", "18.2.0"))
                implementation(npm("react-is", "18.2.0"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.1")
                implementation(project.dependencies.enforcedPlatform("io.micronaut:micronaut-bom:3.10.4"))

                implementation("io.micronaut:micronaut-http-client")
                implementation("io.micronaut:micronaut-http-server-netty")
                implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
                implementation("io.micronaut.aws:micronaut-aws-sdk-v2")
                implementation("io.micronaut.views:micronaut-views-freemarker")
                implementation("io.micronaut.picocli:micronaut-picocli")
                implementation("io.micronaut:micronaut-inject-java")
                implementation("io.micronaut.security:micronaut-security-oauth2")
                implementation("io.micronaut.security:micronaut-security-jwt")
                implementation("software.amazon.awssdk:dynamodb") {
                    exclude(group = "software.amazon.awssdk", module = "apache-client")
                    exclude(group = "software.amazon.awssdk", module = "url-connection-client")
                }
                implementation("software.amazon.awssdk:netty-nio-client")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

                implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.23.1")
                implementation("org.apache.logging.log4j:log4j-core:2.23.1")
                implementation("org.freemarker:freemarker:2.3.33")
                configurations["kapt"].dependencies.addAll(
                    listOf(
                        project.dependencies.create("io.micronaut:micronaut-inject-java:4.5.3"),
                        project.dependencies.create("info.picocli:picocli-codegen:4.7.6"),
                    ),
                )
                if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac")) {
                    implementation("io.micronaut:micronaut-runtime-osx")
                    implementation("net.java.dev.jna:jna")
                    implementation("io.methvin:directory-watcher")
                }
            }
        }
        val jvmTest by getting {
            dependencies {
                configurations["kaptTest"].dependencies.addAll(
                    listOf(
                        project.dependencies.create("io.micronaut:micronaut-inject-java:4.5.3"),
                        project.dependencies.create("info.picocli:picocli-codegen:4.7.6"),
                    ),
                )

                implementation(kotlin("test-junit5"))
                implementation("io.micronaut.test:micronaut-test-junit5")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")

                implementation("org.testcontainers:junit-jupiter")
                implementation("org.testcontainers:localstack")
                implementation("com.amazonaws:aws-java-sdk-core:1.12.750") // testcontainers need it
            }
        }
    }
}

tasks {
    val shadowCreate by creating(ShadowJar::class) {
        manifest {
            attributes["Main-Class"] = "com.github.ikovalyov.MyApp"
        }
        archiveClassifier.set("all")
        from(kotlin.jvm().compilations.getByName("main").output)
        configurations = mutableListOf(kotlin.jvm().compilations.getByName("main").compileDependencyFiles)
    }

    val build by existing {
        dependsOn(shadowCreate)
    }

    val jvmProcessResources by existing(Copy::class) {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE // allow duplicates
    }

    val jvmRun by creating(JavaExec::class) {
        group = "Execution"
        description = "Run the main class with JavaExecTask"
        classpath = sourceSets.findByName("main")!!.runtimeClasspath
        mainClass.set("com.github.ikovalyov.MyApp")
        jvmArgs("-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
    }

    val execDynamoDbScript by creating(JavaExec::class) {
        group = "Execution"
        description = "Run the main class with JavaExecTask"
        classpath = sourceSets.findByName("main")!!.runtimeClasspath
        mainClass.set("com.github.ikovalyov.command.DynamoDbInitCommand")
        jvmArgs("-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
    }
}

kapt {
    arguments {
        arg("project", "${project.group}/${project.name}")
    }
}

spotless {
    kotlin {
        val files = project.fileTree(rootDir)
        files.include("**/*.kt")
        target(files)
        ktlint("1.3.0")
            .editorConfigOverride(
                mapOf(
                    "max_line_length" to "256",
                    "insert_final_newline" to "true",
                ),
            )
    }
    kotlinGradle {
        ktlint("1.3.0")
            .editorConfigOverride(
                mapOf(
                    "max_line_length" to "256",
                    "insert_final_newline" to "true",
                ),
            )
    }
}
