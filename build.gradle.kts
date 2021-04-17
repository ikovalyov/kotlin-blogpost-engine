import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("multiplatform") version "1.5.0-RC"
    kotlin("kapt") version "1.5.0-RC"
    kotlin("plugin.allopen") version "1.5.0-RC"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.diffplug.spotless") version "5.12.1"
}

group = "com.github.ikovalyov"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit", "junit", "4.12")
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
                    jvmTarget = "1.8"
                }
            }
            tasks.named<Test>("${target.name}Test") {
                useJUnitPlatform()
            }
            tasks.named<Jar>("jvmJar") {
                manifest {
                    attributes("Automatic-Module-Name" to moduleName)
                }
            }
        }
    }
    js {
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.github.microutils:kotlin-logging:2.0.4")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.3")
                implementation(project.dependencies.enforcedPlatform("io.micronaut:micronaut-bom:2.4.2"))
                implementation(project.dependencies.enforcedPlatform("software.amazon.awssdk:bom:2.16.43"))

                implementation("io.micronaut:micronaut-http-client")
                implementation("io.micronaut:micronaut-http-server-netty")
                implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
                implementation("software.amazon.awssdk:dynamodb")
                implementation("io.micronaut.views:micronaut-views-freemarker")
                implementation("io.micronaut.picocli:micronaut-picocli")
                implementation("io.micronaut:micronaut-inject-java")

                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

                implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
                implementation("org.apache.logging.log4j:log4j-core:2.14.1")
                implementation("software.amazon.awssdk:dynamodb")
                implementation("software.amazon.awssdk:netty-nio-client")
                implementation("org.freemarker:freemarker:2.3.31")
                configurations["kapt"].dependencies.addAll(
                    listOf(
                        project.dependencies.create("io.micronaut:micronaut-inject-java:2.4.2"),
                        project.dependencies.create("info.picocli:picocli-codegen:4.6.1")
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
                        project.dependencies.create("io.micronaut:micronaut-inject-java:2.4.2"),
                        project.dependencies.create("info.picocli:picocli-codegen:4.6.1")
                    )
                )
                implementation(project.dependencies.enforcedPlatform("org.testcontainers:testcontainers-bom:1.15.3"))

                implementation(kotlin("test-junit5"))
                implementation("io.micronaut.test:micronaut-test-junit5")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

                implementation("org.testcontainers:junit-jupiter")
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
        configurations =
            mutableListOf(kotlin.jvm().compilations.getByName("main").compileDependencyFiles as Configuration)
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

    create("initMongoDbScript", CreateStartScripts::class.java) {
        group = "Execution"
        description = "This command generates tables in the dynamo db required for app to operate"
        applicationName = "dynamo-db-init-command"
        mainClassName = "com.github.ikovalyov.command.DynamoDbInitCommand"
        classpath = files("$buildDir/libs/blog-0.0.1-all.jar")
        outputDir = file("$buildDir/scripts")
        defaultJvmOpts = listOf()
        this.dependsOn("copyJvmToLib")
    }

    create("execMongoDbScript", Exec::class.java) {
        group = "Execution"
        this.executable = "$buildDir/scripts/dynamo-db-init-command"
        dependsOn("initMongoDbScript")
    }
}

kapt {
    arguments {
        arg("project", "${project.group}/${project.name}")
    }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        val files = project.fileTree(rootDir)
        files.include("**/*.kt")

        toggleOffOn()
        target(files)
        ktfmt("0.18").dropboxStyle()
    }
    kotlinGradle {
        val files = project.fileTree(rootDir)
        files.include("**/*.gradle.kts")

        target(files)
        ktlint()
    }
}
