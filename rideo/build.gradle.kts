// File: build.gradle.kts (Project-level)

plugins {
    // Android Gradle Plugin
    id("com.android.application") version "8.4.2" apply false
    // Kotlin
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    // Google Services
    id("com.google.gms.google-services") version "4.4.4" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

allprojects {
    repositories {
        google()       // MUST be first for Firebase
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
