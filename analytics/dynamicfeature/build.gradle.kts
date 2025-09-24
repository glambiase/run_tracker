plugins {
    alias(libs.plugins.runtracker.android.dynamic.feature)
}
android {
    namespace = "com.glambiase.analytics.dynamicfeature"
}

dependencies {
    implementation(libs.androidx.navigation.compose)

    implementation(project(":app"))
    api(projects.analytics.presentation)
    implementation(projects.analytics.domain)
    implementation(projects.analytics.data)
    implementation(projects.core.database)
}