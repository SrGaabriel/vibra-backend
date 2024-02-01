@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
    alias(vibra.plugins.kotlinx.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    api(vibra.kotlinx.datetime)
    implementation(vibra.kotlinx.serialization.json)
    implementation(vibra.kotlinx.coroutines.core)
}