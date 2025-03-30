plugins {
    alias(libs.plugins.runtracker.jvm.library)
}

dependencies {
    implementation(projects.core.domain)
}