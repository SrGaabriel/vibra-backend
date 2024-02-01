plugins {
    kotlin("jvm")
    alias(vibra.plugins.kotlinx.serialization) apply true
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":database"))
    implementation(vibra.bundles.ktor.server)
    implementation(vibra.bundles.koin)
    implementation(vibra.bundles.logging)
    implementation(vibra.bcrypt)
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.10")
    runtimeOnly(vibra.postgresql)
}