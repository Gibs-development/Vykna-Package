import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Keep the tool runnable on Java 11.
    kotlin("jvm") version "1.9.24"
    application
    // Compatible with Gradle 7.x (which runs on Java 11).
    id("org.openjfx.javafxplugin") version "0.0.14"
}

group = "com.vykna"
version = "1.0"

repositories {
    mavenCentral()
}

java {
    // Target Java 11 bytecode. Run Gradle with a Java 11+ JDK.
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }
}

javafx {
    // JavaFX 17 still targets Java 11+.
    version = "17.0.10"
    // Canvas & Scene live in javafx.graphics; keep it explicit.
    modules = listOf("javafx.controls", "javafx.graphics")
}

application {
    // Kotlin file with top-level main() in com.vykna.importer.MainApp.kt
    mainClass.set("com.vykna.importer.MainAppKt")
}

dependencies {
    // Keep deps light: viewer + decoders only.
    implementation("com.google.code.gson:gson:2.11.0")
}
