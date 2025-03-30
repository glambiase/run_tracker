plugins {
    alias(libs.plugins.runtracker.android.library)
}

android {
    namespace = "com.glambiase.run.network"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
}