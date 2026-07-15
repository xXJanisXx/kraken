package dev.xxjanisxx.kraken.repository

import org.gradle.api.provider.Property

/**
 * Represents a Maven repository target for publishing artifacts.
 */
interface KrakenRepository {

    /**
     * The name of the repository. Automatically populated by Gradle using the container key.
     */
    val name: String

    /**
     * The target URL of the Maven repository (e.g., your Reposilite, Nexus, or Artifactory instance).
     */
    val url: Property<String>

    /**
     * The username used for repository authentication.
     */
    val username: Property<String>

    /**
     * The password or token used for repository authentication.
     */
    val password: Property<String>
}