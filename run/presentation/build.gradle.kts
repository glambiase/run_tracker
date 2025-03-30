plugins {
    alias(libs.plugins.runtracker.android.feature.ui)
}

android {
    namespace = "com.glambiase.run.presentation"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.google.maps.android.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)

    implementation(projects.run.domain)
    implementation(projects.core.domain)
}