plugins {
    alias(libs.plugins.runtracker.android.library)
    alias(libs.plugins.runtracker.jvm.ktor)
}

android {
    namespace = "com.glambiase.run.network"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
}