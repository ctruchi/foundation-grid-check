import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "fr.cti"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.htmlparser.jericho:jericho-html:3.4")

    api("io.github.microutils:kotlin-logging:2.1.23")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.11")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "19"
        freeCompilerArgs = listOf("-Xjsr305=strict")
        languageVersion = "1.8"
        apiVersion = "1.8"
    }
}
