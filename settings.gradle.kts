pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("vibra") {
            from(files("vibra.versions.toml"))
        }
    }
}

rootProject.name = "vibra"

include("common")
include("cdn")
include("cdn:client")
include("cdn:server")
include("database")
include("webserver")