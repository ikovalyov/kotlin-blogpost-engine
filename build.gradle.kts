import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.7.10"
    kotlin("kapt") version "1.7.10"
    kotlin("plugin.allopen") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.diffplug.spotless") version "6.7.2"
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
            tasks.named<KotlinCompile>(compileKotlinTaskName) {
                kotlinOptions {
                    jvmTarget = "11"
                    freeCompilerArgs += "-Xopt-in=org.mylibrary.OptInAnnotation"
                }
            }
            tasks.named<Test>("${target.name}Test") {
                useJUnitPlatform()
                testLogging {
                    events("passed", "skipped", "failed")
                    showStackTraces = true
                    exceptionFormat = TestExceptionFormat.FULL
                }
            }
            tasks.named<Jar>("jvmJar") {
                manifest {
                    attributes("Automatic-Module-Name" to moduleName)
                }
            }
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.github.microutils:kotlin-logging:2.1.23")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
                implementation("com.benasher44:uuid:0.5.0")
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
                        "org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:0.0.1-pre.335"
                    )
                )
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-table")
                implementation(npm("react", "17.0.2"))
                implementation(npm("react-dom", "17.0.2"))
                implementation(npm("react-is", "17.0.2"))

                implementation("org.jetbrains.kotlin-wrappers:kotlin-styled")
                implementation(npm("styled-components", "5.2.3"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.2")
                implementation(project.dependencies.enforcedPlatform("io.micronaut:micronaut-bom:3.6.3"))

                implementation("io.micronaut:micronaut-http-client")
                implementation("io.micronaut:micronaut-http-server-netty")
                implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
                implementation("io.micronaut.aws:micronaut-aws-sdk-v2")
                implementation("io.micronaut.views:micronaut-views-freemarker")
                implementation("io.micronaut.picocli:micronaut-picocli")
                implementation("io.micronaut:micronaut-inject-java")
                implementation("software.amazon.awssdk:dynamodb") {
                    exclude(group = "software.amazon.awssdk", module = "apache-client")
                    exclude(group = "software.amazon.awssdk", module = "url-connection-client")
                }
                implementation("software.amazon.awssdk:netty-nio-client")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

                implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.18.0")
                implementation("org.apache.logging.log4j:log4j-core:2.18.0")
                implementation("org.freemarker:freemarker:2.3.31")
                configurations["kapt"].dependencies.addAll(
                    listOf(
                        project.dependencies.create("io.micronaut:micronaut-inject-java:3.6.3"),
                        project.dependencies.create("info.picocli:picocli-codegen:4.6.3")
                    )
                )
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
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
                        project.dependencies.create("io.micronaut:micronaut-inject-java:3.6.3"),
                        project.dependencies.create("info.picocli:picocli-codegen:4.6.3")
                    )
                )
                implementation(project.dependencies.enforcedPlatform("org.testcontainers:testcontainers-bom:1.16.2"))

                implementation(kotlin("test-junit5"))
                implementation("io.micronaut.test:micronaut-test-junit5")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

                implementation("org.testcontainers:junit-jupiter")
                implementation("org.testcontainers:localstack")
                implementation("com.amazonaws:aws-java-sdk-core:1.12.301") // testcontainers need it
            }
        }
    }
}

tasks {
    val shadowCreate by creating(ShadowJar::class) {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE // allow duplicates
        mergeServiceFiles()
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

    create("jvmRun", JavaExec::class.java) {
        group = "Execution"
        description = "Run the main class with JavaExecTask"
        classpath = sourceSets.findByName("main")!!.runtimeClasspath
        main = "com.github.ikovalyov.MyApp"

        jvmArgs("-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
    }

    create("copyJvmToLib", Copy::class.java) {
        dependsOn(shadowCreate)
        from(layout.buildDirectory.dir("$buildDir/libs"))
        into(layout.buildDirectory.dir("$buildDir/lib"))
    }

    create("initDynamoDbScript", CreateStartScripts::class.java) {
        group = "Execution"
        description = "This command generates tables in the dynamo db required for app to operate"
        applicationName = "dynamo-db-init-command"
        mainClassName = "com.github.ikovalyov.command.DynamoDbInitCommand"
        classpath = files("$buildDir/libs/blog-0.0.1-all.jar")
        outputDir = file("$buildDir/scripts")
        defaultJvmOpts = listOf()
        this.dependsOn("copyJvmToLib")
    }

    create("execDynamoDbScript", Exec::class.java) {
        group = "Execution"
        this.executable = "$buildDir/scripts/dynamo-db-init-command"
        dependsOn("initDynamoDbScript")
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
        ktlint("0.42.1").userData(
            mapOf(
                "max_line_length" to "256",
                "insert_final_newline" to "true"
            )
        )
    }
    kotlinGradle {
        ktlint("0.42.1").userData(
            mapOf(
                "max_line_length" to "125",
                "insert_final_newline" to "true"
            )
        )
    }
}

tasks.named<KotlinJsCompile>("compileKotlinJs").configure {
    kotlinOptions.moduleKind = "amd"
    kotlinOptions.sourceMap = true
    kotlinOptions.sourceMapEmbedSources = "always"
}

rootProject.plugins.withType<NodeJsRootPlugin> {
    rootProject.the<NodeJsRootExtension>().versions.webpackDevServer.version = "4.0.0"
}
