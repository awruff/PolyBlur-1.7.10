plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "PolyBlur-1.7.10"

includeBuild("forge")
includeBuild("ornithe")
