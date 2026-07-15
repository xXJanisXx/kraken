import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("maven-publish")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.publish)
}

group = "dev.xxjanisxx"
description = "A Gradle plugin to publish artifacts to Maven Central or Custom Repositories."
version = "1.1.0"

gradlePlugin {
    website = "https://github.com/xXJanisXx/kraken"
    vcsUrl = "https://github.com/xXJanisXx/kraken.git"

    plugins {
        create("kraken") {
            id = "dev.xxjanisxx.kraken"
            implementationClass = "dev.xxjanisxx.kraken.Kraken"
            displayName = "Kraken"
            description = "A Gradle plugin to publish artifacts to Maven Central or Custom Repositories."
            tags = listOf("publishing", "maven", "maven-central", "sonatype", "central-portal")
        }
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(gradleApi())
    testImplementation(gradleTestKit())

    implementation(libs.central.publisher)
    testImplementation(libs.kotlin.test)
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        languageVersion = KotlinVersion.KOTLIN_2_4
        apiVersion = KotlinVersion.KOTLIN_2_4
    }
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.test {
    useJUnitPlatform()
}
