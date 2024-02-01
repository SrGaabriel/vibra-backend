@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
    alias(vibra.plugins.kotlinx.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(vibra.bundles.ktor.server)
    implementation(vibra.bundles.koin)
    implementation(vibra.ktor.http)
}