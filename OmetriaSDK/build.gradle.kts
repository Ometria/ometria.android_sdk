val versionName = "1.10.1"

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.android.ometriasdk"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Android Core
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.security:security-crypto:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.1.4")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.7")

    // Add the SDK for Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging:24.1.1")
}

// Publishing
extra.apply {
    set("PUBLISH_GROUP_ID", "com.ometria")
    set("PUBLISH_ARTIFACT_ID", "android-sdk")
    set("PUBLISH_VERSION", versionName)
}

apply("${rootProject.projectDir}/scripts/publish-mavencentral.gradle")
