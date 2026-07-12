# Kraken

A Gradle plugin to publish artifacts to Maven Central.

## Usage

```kotlin
plugins {
    id("dev.xxjanisxx.kraken") version "1.0.0"
}

group = "com.example"
version = "1.0.0"

maven {
    artifactId = "example-lib" // default: project.name
    url = "https://github.com/example/example-repository"
    description = "An example description"

    publishingType = PublishingType.USER_MANAGED // default: AUTOMATIC

    licenses {
        license {
            name = "MIT"
            url = "https://opensource.org/licenses/MIT"
        }
    }
    developers {
        developer {
            id = "john"
            name = "John Doe"
            email = "john@example.com"
        }
    }
    scm {
        url = "https://github.com/example/example-repository"
        connection = "scm:git:https://github.com/example/example-repository.git"
        developerConnection = "scm:git:ssh://git@github.com/example/example-repository.git"
    }
    ciManagement {
        system = "GitHub Actions"
        url = "https://github.com/example/example-repository/actions"
    }
    organization {
        name = "Example Org"
        url = "https://example.com"
    }

    // Credentials and signing keys default to the environment variables below,
    // but can be overridden here if needed:
    // username = "..."
    // password = "..."
    // signingKey = "..."
    // signingPassword = "..."
    // signingKeyId = "..."
}
```

## Environment variables

| Variable           | Purpose                       |
|--------------------|-------------------------------|
| `MAVEN_USER`       | Central Portal username/token |
| `MAVEN_TOKEN`      | Central Portal password/token |
| `SIGNING_KEY`      | ASCII-armored PGP secret key  |
| `SIGNING_PASSWORD` | Passphrase for `SIGNING_KEY`  |
| `SIGNING_KEY_ID`   | Optional key id (for subkeys) |

## Tasks

| Task                     | Description                                     |
|--------------------------|-------------------------------------------------|
| `publishToCentralPortal` | Uploads the bundle to the Sonatype Central Portal |
| `generateBundle`         | Creates the upload bundle                       |
| `checksumBundle`         | Generates `.md5` / `.sha1` checksums            |
