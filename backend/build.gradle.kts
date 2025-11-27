
plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    application
}

group = "com.emmascode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktor_version = "2.3.7"
val logback_version = "1.4.11"


dependencies {
    // Ktor Server
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Exposed ORM + PostgreSQL
    implementation("org.jetbrains.exposed:exposed-core:0.53.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.53.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.53.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.53.0")
    implementation("org.postgresql:postgresql:42.7.3")

    // Authentication & Authorization
    implementation("io.ktor:ktor-server-auth-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:${ktor_version}")
// CORS
    implementation("io.ktor:ktor-server-cors-jvm:${ktor_version}")
// Status Pages
    implementation("io.ktor:ktor-server-status-pages-jvm:${ktor_version}")
// Call Logging
    implementation("io.ktor:ktor-server-call-logging-jvm:${ktor_version}")

    // Connection Pooling
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Password Hashing
    implementation("org.mindrot:jbcrypt:0.4")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
    // Load .env files
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-tests:2.3.7")


}

kotlin {
    jvmToolchain(21)
}

application {
    // Replace with your main class
    mainClass.set("com.emmascode.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
