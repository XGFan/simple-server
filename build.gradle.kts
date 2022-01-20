import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "com.test4x"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    implementation("com.sparkjava", "spark-core", "2.9.3")
    implementation("org.slf4j", "slf4j-simple", "1.7.33")
    implementation("com.jayway.jsonpath", "json-path", "2.6.0")
    implementation("com.lectra", "koson", "1.2.1")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}