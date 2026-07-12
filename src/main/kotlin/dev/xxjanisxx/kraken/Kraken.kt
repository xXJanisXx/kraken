package dev.xxjanisxx.kraken

import dev.xxjanisxx.kraken.extensions.MavenExtension
import dev.xxjanisxx.kraken.publish.PublishingType
import net.thebugmc.gradle.sonatypepublisher.CentralPortalExtension
import net.thebugmc.gradle.sonatypepublisher.SonatypeCentralPortalPublisherPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.plugins.signing.SigningExtension

class Kraken : Plugin<Project> {

    override fun apply(project: Project) {
        val logger = project.logger
        val providers = project.providers

        val maven = project.extensions.create("maven", MavenExtension::class.java)
        maven.artifactId.convention(providers.provider { project.name })
        maven.publishingType.convention(PublishingType.AUTOMATIC)
        maven.username.convention(providers.environmentVariable("MAVEN_USER"))
        maven.password.convention(providers.environmentVariable("MAVEN_TOKEN"))
        maven.signingKey.convention(providers.environmentVariable("SIGNING_KEY"))
        maven.signingPassword.convention(providers.environmentVariable("SIGNING_PASSWORD"))
        maven.signingKeyId.convention(providers.environmentVariable("SIGNING_KEY_ID"))

        project.pluginManager.apply(SonatypeCentralPortalPublisherPlugin::class.java)

        val centralPortal = project.extensions.getByType(CentralPortalExtension::class.java)
        centralPortal.username.convention(maven.username)
        centralPortal.password.convention(maven.password)
        centralPortal.name.convention(maven.artifactId)
        centralPortal.publishingType.convention(maven.publishingType.map { it.toCentral() })
        centralPortal.pom { pom -> maven.configurePom(pom) }

        project.afterEvaluate {
            configureSigning(project, maven, logger)
        }
    }

    private fun configureSigning(project: Project, maven: MavenExtension, logger: Logger) {
        val signing = project.extensions.getByType(SigningExtension::class.java)
        val key = maven.signingKey.orNull
        val password = maven.signingPassword.orNull
        val keyId = maven.signingKeyId.orNull

        if (key.isNullOrBlank()) {
            signing.isRequired = false
            logger.warn("No signing key found (SIGNING_KEY). Artifacts will be unsigned.")
            return
        }

        if (keyId.isNullOrBlank()) {
            signing.useInMemoryPgpKeys(key, password)
        } else {
            signing.useInMemoryPgpKeys(keyId, key, password)
        }
    }
}
