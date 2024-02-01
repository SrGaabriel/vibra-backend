@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(vibra.bundles.koin)
}