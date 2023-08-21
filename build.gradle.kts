plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    implementation("com.squareup.okhttp3:mockwebserver:4.11.0")
    implementation("io.qameta.allure:allure-java-commons:2.23.0")
}