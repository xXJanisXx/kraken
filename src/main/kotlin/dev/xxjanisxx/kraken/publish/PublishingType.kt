package dev.xxjanisxx.kraken.publish

import net.thebugmc.gradle.sonatypepublisher.PublishingType

/**
 * Controls what happens after a bundle is successfully validated by the Sonatype Central Portal.
 */
enum class PublishingType {

    /**
     * The deployment is released to Maven Central automatically once validation passes. Default.
     */
    AUTOMATIC,

    /**
     * The deployment is held in the Central Portal for you to release manually via the web UI.
     */
    USER_MANAGED;

    internal fun toCentral(): PublishingType = when (this) {
        AUTOMATIC -> PublishingType.AUTOMATIC
        USER_MANAGED -> PublishingType.USER_MANAGED
    }
}