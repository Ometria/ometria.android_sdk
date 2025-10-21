// Top-level build file where you can add configuration options common to all sub-projects/modules.
import java.util.Properties

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.10.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("io.github.gradle-nexus.publish-plugin:io.github.gradle-nexus.publish-plugin.gradle.plugin:2.0.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

// Load properties from local.properties
val localProps = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        load(localPropsFile.inputStream())
    }
}

// Configure Nexus Publish plugin to manage staging and release
nexusPublishing {
    packageGroup.set("com.ometria.android-sdk")
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://ossrh-snapshot-api.central.sonatype.com/service/local/"))
            username.set(localProps.getProperty("mavenCentralUsername") ?: "")
            password.set(localProps.getProperty("mavenCentralPassword") ?: "")
        }
    }
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.layout.buildDirectory)
    }
}
