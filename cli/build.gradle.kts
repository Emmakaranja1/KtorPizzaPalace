plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.emmascode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // ---- Testing ----
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-mock:2.3.7")

    // ---- Ktor Client ----
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-client-logging:2.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")

    // ---- CLI Framework ----
    implementation("com.github.ajalt.clikt:clikt:4.2.1")

    // ---- Pretty Output ----
    implementation("com.github.ajalt.mordant:mordant:2.2.0")

    // ---- Env Vars ----
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
}


kotlin { jvmToolchain(21) }

application {
    mainClass.set("com.emmascode.MainKt")
}

tasks.test { useJUnitPlatform() }

tasks.jar {
    manifest { attributes["Main-Class"] = "com.emmascode.MainKt" }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

// Configure the existing distZip task instead of creating a new one
tasks.named<Zip>("distZip") {
    dependsOn(tasks.jar)
    from(tasks.jar)
    archiveFileName.set("pizza-cli-${project.version}.zip")
    destinationDirectory.set(file("$buildDir/distributions"))
}


