import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.api.tasks.bundling.Zip

plugins {
    java
    war
    application
    id("org.jetbrains.kotlin.jvm") version "1.5.21"
}

group = "runerogue"
version = "2.0.10" // change if you want

application {
    mainClass.set("io.xeros.Server")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11" // safe + compatible
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}


repositories {
    mavenCentral()
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs(
        "-Dio.netty.leakDetection.level=advanced"
    )
}

sourceSets {
    named("main") {
        java.srcDir("src")
        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            kotlin.srcDir("srcKotlin")
        }
        resources.srcDir("resources")
    }
    named("test") {
        java.srcDir("test")
        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            kotlin.srcDir("testKotlin")
        }
    }
}

dependencies {
    // Local jars in /deps
    implementation(fileTree("deps") { include("*.jar") })

    // Lombok (your original had only annotationProcessor; add compileOnly too)
    compileOnly("org.projectlombok:lombok:1.18.12")
    annotationProcessor("org.projectlombok:lombok:1.18.12")
    testCompileOnly("org.projectlombok:lombok:1.18.12")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.12")

    // Reflections
    implementation("org.reflections:reflections:0.9.12")

    // Jackson (as you had)
    implementation("com.fasterxml.jackson.core:jackson-core:2.8.6")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.8.6")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.8.6")

    implementation("de.svenkubiak:jBCrypt:0.4.1")
    implementation("com.github.cage:cage:1.0")

    // Logging
    implementation("org.slf4j:slf4j-api:1.7.25")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")

    // MySQL
    implementation("mysql:mysql-connector-java:8.0.25")

    // Discord bot
    implementation("net.dv8tion:JDA:4.2.1_253")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation("com.google.code.gson:gson:2.10.1")
    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
    // Flyway
    implementation("org.flywaydb:flyway-core:7.11.0")

    // Commons Net
    implementation("commons-net:commons-net:3.8.0")

    // HikariCP
    implementation("com.zaxxer:HikariCP:3.4.5")

    // Jsoup
    implementation("org.jsoup:jsoup:1.14.2")

    // Netty
    implementation("io.netty:netty-all:4.1.68.Final")

    // Kotlin stdlib (usually auto-added by kotlin plugin, but explicit is fine)
    implementation(kotlin("stdlib"))

    // Tests (pick ONE approach; this keeps Jupiter like your config intended)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

    // If you *actually* have JUnit4 tests, keep this:
    testImplementation("junit:junit:4.13.1")

        // Flyway
        implementation("org.flywaydb:flyway-core:7.11.0")

        // JSON Simple (ItemList)
        implementation("com.googlecode.json-simple:json-simple:1.1.1")

        // SLF4J API (+ Logback runtime implementation)
        implementation("org.slf4j:slf4j-api:1.7.25")
        runtimeOnly("ch.qos.logback:logback-classic:1.2.3")

        // Guava (Preconditions, ThreadFactoryBuilder)
        implementation("com.google.guava:guava:31.1-jre")

        // Netty
        implementation("io.netty:netty-all:4.1.68.Final")

}

tasks.test {
    useJUnitPlatform()
}

/**
 * Add /etc into the application distribution zip under: <projectName>/bin/etc
 * (matches the intent of your old distZip block)
 */
tasks.named<Zip>("distZip") {
    from("etc") {
        into("${project.name}/bin/etc")
    }
}

// Optional: also for distTar if you use it
// tasks.named<Tar>("distTar") {
//     from("etc") {
//         into("${project.name}/bin/etc")
//     }
// }
