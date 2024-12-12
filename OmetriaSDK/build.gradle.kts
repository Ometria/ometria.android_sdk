val versionName = "1.8.0"

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.android.ometriasdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        targetSdk = 33
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

val kotlinVersion = rootProject.extra.get("kotlin_version") as String

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Android Core
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.security:security-crypto:1.0.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.7")

    // Add the SDK for Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging:24.1.0")
}

// Publishing
extra.apply {
    set("PUBLISH_GROUP_ID", "com.ometria")
    set("PUBLISH_ARTIFACT_ID", "android-sdk")
    set("PUBLISH_VERSION", versionName)
}

apply("${rootProject.projectDir}/scripts/publish-mavencentral.gradle")
