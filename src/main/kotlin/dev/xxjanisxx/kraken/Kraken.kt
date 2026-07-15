package dev.xxjanisxx.kraken

import dev.xxjanisxx.kraken.extensions.MavenExtension
import dev.xxjanisxx.kraken.publish.PublishingType
import net.thebugmc.gradle.sonatypepublisher.CentralPortalExtension
import net.thebugmc.gradle.sonatypepublisher.SonatypeCentralPortalPublisherPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.SigningExtension

class Kraken : Plugin<Project> {

    override fun apply(project: Project) {
        val providers = project.providers

        val maven = project.extensions.create("maven", MavenExtension::class.java, project.objects)
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

        project.plugins.withId("maven-publish") {
            val publishing = project.extensions.getByType(PublishingExtension::class.java)

            project.afterEvaluate {
                maven.repositories.forEach { customRepo ->
                    publishing.repositories.maven { repo ->
                        repo.name = customRepo.name
                        repo.url = project.uri(customRepo.url.get())

                        if (customRepo.username.isPresent) {
                            repo.credentials { credentials ->
                                credentials.username = customRepo.username.orNull
                                credentials.password = customRepo.password.orNull
                            }
                        }
                    }
                }
            }
        }

        project.afterEvaluate {
            configureSigning(project, maven)
        }
    }

    private fun configureSigning(project: Project, maven: MavenExtension) {
        val signing = project.extensions.getByType(SigningExtension::class.java)
        val key = maven.signingKey.orNull
        val password = maven.signingPassword.orNull
        val keyId = maven.signingKeyId.orNull

        if (key.isNullOrBlank()) {
            signing.isRequired = false
            return
        }

        if (keyId.isNullOrBlank()) {
            signing.useInMemoryPgpKeys(key, password)
        } else {
            signing.useInMemoryPgpKeys(keyId, key, password)
        }
    }
}