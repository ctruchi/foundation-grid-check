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

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "19"
        freeCompilerArgs = listOf("-Xjsr305=strict")
        languageVersion = "1.8"
        apiVersion = "1.8"
    }
}
