import proguard.gradle.ProGuardTask
import org.gradle.jvm.tasks.Jar
import java.security.MessageDigest
import java.util.Properties

/**
 * Resolve the GitHub CLI executable.
 * - If GH_EXE is set and points to an existing file, use it.
 * - Otherwise try common Windows install paths.
 * - Otherwise fall back to "gh" (PATH).
 */
fun ghExe(): String {
    val env = System.getenv("GH_EXE")
    if (!env.isNullOrBlank() && file(env).exists()) return env

    val candidates = listOf(
        "C:\\Program Files\\GitHub CLI\\gh.exe",
        "C:\\Program Files (x86)\\GitHub CLI\\gh.exe"
    )
    return candidates.firstOrNull { file(it).exists() } ?: "gh"
}

/**
 * Compute SHA-256 hex for a file (lowercase).
 */
fun sha256Hex(file: java.io.File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
        val buf = ByteArray(1024 * 1024)
        while (true) {
            val read = input.read(buf)
            if (read <= 0) break
            digest.update(buf, 0, read)
        }
    }
    return digest.digest().joinToString("") { b -> "%02x".format(b) }
}

/**
 * Bump patch version (MAJOR.MINOR.PATCH).
 */
fun bumpPatch(v: String): String {
    val parts = v.trim().split(".")
    if (parts.size != 3) throw GradleException("clientVersion must be MAJOR.MINOR.PATCH (e.g. 0.0.1) but was: $v")
    val major = parts[0].toInt()
    val minor = parts[1].toInt()
    val patch = parts[2].toInt()
    return "$major.$minor.${patch + 1}"
}

/**
 * release.properties management (keeps publishClient one-command).
 *
 * Create release.properties at repo root:
 *   clientVersion=0.0.1
 */
val releasePropsFile = file("release.properties")

fun readClientVersion(): String {
    if (!releasePropsFile.exists()) return "0.0.0"
    val p = Properties()
    releasePropsFile.inputStream().use { p.load(it) }
    return p.getProperty("clientVersion") ?: "0.0.0"
}

fun writeClientVersion(v: String) {
    val p = Properties()
    p.setProperty("clientVersion", v)
    releasePropsFile.outputStream().use { out ->
        p.store(out, "Auto-managed by publishClient")
    }
}

/**
 * Check if a GitHub release tag exists using gh CLI.
 */
fun ghReleaseExists(repo: String, tag: String): Boolean {
    val result = exec {
        isIgnoreExitValue = true
        commandLine(ghExe(), "release", "view", tag, "-R", repo)
        standardOutput = org.gradle.internal.io.NullOutputStream.INSTANCE
        errorOutput = org.gradle.internal.io.NullOutputStream.INSTANCE
    }
    return result.exitValue == 0
}

plugins {
    java
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

application {
    mainClass.set("com.client.Client")
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:none")
    options.isFork = true
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.runelite.net")
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("net.sf.proguard:proguard-gradle:6.0.2")
    }
}

sourceSets {
    named("main") {
        java.srcDirs("src/main/java")
        resources.srcDirs(
            "runelite/http-api/src/main/resources",
            "runelite/runelite-client/src/main/resources"
        )
    }
}

dependencies {
    /* Core */
    implementation("com.thoughtworks.xstream:xstream:1.4.7")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.google.code.gson:gson:2.9.0")

    /* Jackson */
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3")

    /* Desktop / UI */
    implementation("com.dorkbox:Notify:3.7")
    implementation("com.intellij:forms_rt:7.0.3")

    /* Reflection */
    implementation("net.oneandone.reflections8:reflections8:0.11.7")

    /* Runelite */
    implementation("net.runelite.pushingpixels:trident:1.5.00")
    implementation("net.runelite.pushingpixels:substance:8.0.02")
    implementation("net.runelite:discord:1.1")
    implementation("com.google.inject:guice:4.2.2")
    implementation("com.squareup.okhttp3:okhttp:4.3.0")

    /* Apache extras */
    implementation("org.apache.commons:commons-csv:1.7")
    implementation("org.apache.commons:commons-text:1.8")
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4")

    /* Lombok */
    compileOnly("org.projectlombok:lombok:1.18.8")
    annotationProcessor("org.projectlombok:lombok:1.18.8")

    /* Testing */
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}

/**
 * Runnable fat jar (optional).
 */
tasks.register<Jar>("runnableJar") {
    group = "build"
    description = "Builds a runnable (fat) jar with all dependencies."

    archiveFileName.set("ClientRunnable.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "com.client.Client"
    }

    from(sourceSets["main"].output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith(".jar") }
            .map { zipTree(it) }
    })

    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

/**
 * Standard non-obfuscated fat jar — client.jar
 */
val createStandardJar = tasks.register<Jar>("createStandardJar") {
    group = "build"
    description = "Builds client.jar (fat jar, non-obfuscated)."

    archiveFileName.set("client.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets["main"].output)

    manifest {
        attributes["Main-Class"] = "com.client.Client"
    }

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith(".jar") }
            .map { zipTree(it) }
    })

    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

/**
 * Writes build/libs/client.jar.sha256
 */
val writeStandardSha256 = tasks.register("writeStandardSha256") {
    group = "build"
    description = "Writes build/libs/client.jar.sha256"

    dependsOn(createStandardJar)

    doLast {
        val jarFile = createStandardJar.get().archiveFile.get().asFile
        val shaFile = jarFile.parentFile.resolve("${jarFile.name}.sha256")

        val hash = sha256Hex(jarFile)
        shaFile.writeText("$hash  ${jarFile.name}\n", Charsets.US_ASCII)

        println("Wrote SHA-256: ${shaFile.absolutePath}")
    }
}

createStandardJar.configure {
    finalizedBy(writeStandardSha256)
}

/**
 * ProGuard obfuscation — outputs build/libs/deploy/client.jar (kept for later).
 */
val obfuscateStandard = tasks.register<ProGuardTask>("obfuscateStandard") {
    group = "build"
    description = "Obfuscates client.jar -> build/libs/deploy/client.jar"

    configuration("proguard.conf")
    dependsOn(createStandardJar)

    configurations.runtimeClasspath.get().forEach {
        libraryjars(it)
    }

    injars("build/libs/client.jar")
    outjars("build/libs/deploy/client.jar")
}

/**
 * Writes build/libs/deploy/client.jar.sha256
 */
val writeDeploySha256 = tasks.register("writeDeploySha256") {
    group = "build"
    description = "Writes build/libs/deploy/client.jar.sha256"

    dependsOn(obfuscateStandard)

    doLast {
        val jarFile = file("build/libs/deploy/client.jar")
        if (!jarFile.exists()) throw GradleException("Missing obfuscated jar: ${jarFile.absolutePath}")

        val shaFile = file("build/libs/deploy/client.jar.sha256")
        val hash = sha256Hex(jarFile)
        shaFile.writeText("$hash  ${jarFile.name}\n", Charsets.US_ASCII)

        println("Wrote SHA-256: ${shaFile.absolutePath}")
    }
}

obfuscateStandard.configure {
    finalizedBy(writeDeploySha256)
}

/**
 * publishClient
 *
 * One-command publish:
 * - Auto-bumps patch version (stored in release.properties)
 * - Builds build/libs/client.jar + build/libs/client.jar.sha256
 * - Creates a NEW GitHub Release with the bumped tag (vX.Y.Z) and uploads the assets
 *
 * Requires GitHub CLI installed + authenticated:
 *   gh auth login
 */
tasks.register("publishClient") {
    group = "release"
    description = "Auto-bumps version and publishes client.jar + sha256 to GitHub Releases."

    dependsOn(createStandardJar)
    dependsOn(writeStandardSha256)

    doLast {
        val repo = "Gibs-Development/arwyn-client-releases"

        val jarFile = file("build/libs/client.jar")
        val shaFile = file("build/libs/client.jar.sha256")

        if (!jarFile.exists()) throw GradleException("Missing: ${jarFile.absolutePath}")
        if (!shaFile.exists()) throw GradleException("Missing: ${shaFile.absolutePath}")

        val current = readClientVersion()

        // Find next available tag (in case a tag already exists)
        var next = bumpPatch(current)
        var tag = "v$next"

        var guard = 0
        while (ghReleaseExists(repo, tag)) {
            next = bumpPatch(next)
            tag = "v$next"
            guard++
            if (guard > 50) throw GradleException("Could not find a free version tag after 50 bumps (starting from $current).")
        }

        // Create the release + upload assets
        exec {
            commandLine(
                ghExe(), "release", "create", tag,
                "-R", repo,
                "--title", tag,
                "--notes", "Automated client build ($tag)",
                jarFile.absolutePath,
                shaFile.absolutePath
            )
        }

        // Only bump local version AFTER successful publish (safer)
        writeClientVersion(next)

        println("Published $tag to $repo")
        println("Updated release.properties -> clientVersion=$next")
    }
}

/**
 * Convenience task: build everything needed for release
 */
tasks.register("buildJars") {
    group = "build"
    description = "Builds client.jar, sha, and obfuscated jar + sha."

    dependsOn(createStandardJar)
    dependsOn(obfuscateStandard)
    dependsOn(writeStandardSha256)
    dependsOn(writeDeploySha256)
}
