package dev.xxjanisxx.kraken.extensions

import dev.xxjanisxx.kraken.publish.PublishingType
import dev.xxjanisxx.kraken.repository.KrakenRepository
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomCiManagement
import org.gradle.api.publish.maven.MavenPomDeveloperSpec
import org.gradle.api.publish.maven.MavenPomLicenseSpec
import org.gradle.api.publish.maven.MavenPomOrganization
import org.gradle.api.publish.maven.MavenPomScm
import javax.inject.Inject

/** The maven extension used to configure publishing to Maven Central or custom repositories. */
abstract class MavenExtension @Inject constructor(
    objects: ObjectFactory
) {

    /** The Maven artifactId. Defaults to the project name. */
    abstract val artifactId: Property<String>

    /** Project URL for the POM. */
    abstract val url: Property<String>

    /** Project description for the POM. */
    abstract val description: Property<String>

    /** What happens after Central validation. Defaults to AUTOMATIC. */
    abstract val publishingType: Property<PublishingType>

    /** Username. Defaults to the MAVEN_USER environment variable. */
    abstract val username: Property<String>

    /** Password/token. Defaults to the MAVEN_TOKEN environment variable. */
    abstract val password: Property<String>

    /** PGP secret key. Defaults to the SIGNING_KEY environment variable. */
    abstract val signingKey: Property<String>

    /** Passphrase for the signing key. Defaults to the SIGNING_PASSWORD environment variable. */
    abstract val signingPassword: Property<String>

    /** Optional PGP key id. Defaults to the SIGNING_KEY_ID environment variable. */
    abstract val signingKeyId: Property<String>

    /** Custom repositories for publishing (e.g., Reposilite, Nexus). */
    val repositories: NamedDomainObjectContainer<KrakenRepository> =
        objects.domainObjectContainer(KrakenRepository::class.java)

    private val developers = mutableListOf<Action<in MavenPomDeveloperSpec>>()
    private val licenses = mutableListOf<Action<in MavenPomLicenseSpec>>()
    private val scm = mutableListOf<Action<in MavenPomScm>>()
    private val ciManagement = mutableListOf<Action<in MavenPomCiManagement>>()
    private val organization = mutableListOf<Action<in MavenPomOrganization>>()

    /** Configure custom repositories. */
    fun repositories(action: Action<in NamedDomainObjectContainer<KrakenRepository>>) {
        action.execute(repositories)
    }

    /** Declare project developers. */
    fun developers(action: Action<in MavenPomDeveloperSpec>) {
        developers.add(action)
    }

    /** Declare project licenses. */
    fun licenses(action: Action<in MavenPomLicenseSpec>) {
        licenses.add(action)
    }

    /** Declare source control management info. */
    fun scm(action: Action<in MavenPomScm>) {
        scm.add(action)
    }

    /** Declare continuous integration info. */
    fun ciManagement(action: Action<in MavenPomCiManagement>) {
        ciManagement.add(action)
    }

    /** Declare the owning organization. */
    fun organization(action: Action<in MavenPomOrganization>) {
        organization.add(action)
    }

    /** Applies the configured metadata onto the published POM. */
    internal fun configurePom(pom: MavenPom) {
        if (url.isPresent) pom.url.set(url)
        if (description.isPresent) pom.description.set(description)
        developers.forEach { pom.developers(it) }
        licenses.forEach { pom.licenses(it) }
        scm.forEach { pom.scm(it) }
        ciManagement.forEach { pom.ciManagement(it) }
        organization.forEach { pom.organization(it) }
    }
}