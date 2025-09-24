plugins {
    alias(libs.plugins.runtracker.android.dynamic.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.glambiase.analytics.dynamicfeature"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":app"))
    api(projects.analytics.presentation)
    implementation(projects.analytics.domain)
    implementation(projects.analytics.data)
    implementation(projects.core.database)
}