import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("multiplatform") version "1.4.31"
    kotlin("kapt") version "1.4.31"
    kotlin("plugin.allopen") version "1.4.31"
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
                api(kotlin("stdlib-jdk8"))
                api(kotlin("reflect"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.3")
                implementation(project.dependencies.enforcedPlatform("io.micronaut:micronaut-bom:2.3.3"))
                implementation(project.dependencies.enforcedPlatform("software.amazon.awssdk:bom:2.16.12"))
                implementation("io.micronaut:micronaut-http-server-netty")
                implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
                implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")
                implementation("software.amazon.awssdk:dynamodb")
                implementation("io.micronaut.views:micronaut-views-freemarker")
                implementation("org.freemarker:freemarker:2.3.30")
                configurations["kapt"].dependencies.add(
                    project.dependencies.create("io.micronaut:micronaut-inject-java:2.4.2")
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
                api(kotlin("test-junit5"))
                implementation("io.micronaut.test:micronaut-test-junit5")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
            }
        }
    }
}

tasks {
    create("jvmRun", JavaExec::class.java) {
        group = "Execution"
        description = "Run the main class with JavaExecTask"
        classpath = sourceSets.findByName("main")!!.runtimeClasspath
        main = "com.github.ikovalyov.MyApp"

        jvmArgs("-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
    }
}