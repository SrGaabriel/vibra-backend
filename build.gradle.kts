@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(vibra.plugins.kotlin.jvm)
    alias(vibra.plugins.kotlinx.serialization) apply true
}

subprojects {
    group = "io.github.vibraplatform"
    version = "1.0"
}

repositories {
    mavenCentral()
}