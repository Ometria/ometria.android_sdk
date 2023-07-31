val versionName = "1.5.1"

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 29
    }

    buildTypes {
        debug {
            buildConfigField("String", "SDK_VERSION_NAME", "\"$versionName\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            buildConfigField("String", "SDK_VERSION_NAME", "\"$versionName\"")
        }
    }
}

val kotlinVersion = rootProject.extra.get("kotlin_version") as String

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Android Core
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-process:2.5.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")

    // Add the SDK for Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging:23.0.8")
}

// Publishing
extra.apply {
    set("PUBLISH_GROUP_ID", "com.ometria")
    set("PUBLISH_ARTIFACT_ID", "android-sdk")
    set("PUBLISH_VERSION", versionName)
}

apply("${rootProject.projectDir}/scripts/publish-mavencentral.gradle")