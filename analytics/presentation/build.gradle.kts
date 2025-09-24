plugins {
    alias(libs.plugins.runtracker.android.feature.ui)
}

android {
    namespace = "com.glambiase.analytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
}