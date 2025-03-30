plugins {
    alias(libs.plugins.runtracker.android.library)
}

android {
    namespace = "com.glambiase.core.database"
}

dependencies {
    implementation(libs.org.mongodb.bson)

    implementation(projects.core.domain)
}