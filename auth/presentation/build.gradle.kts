plugins {
    alias(libs.plugins.runtracker.android.feature.ui)
}

android {
    namespace = "com.glambiase.auth.presentation"
}

dependencies {
    implementation(projects.auth.domain)
    implementation(projects.core.domain)
}