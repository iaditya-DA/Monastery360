plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.monastery360"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.monastery360"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = false
    }
}

dependencies {
    // ===== CORE LIBRARIES =====
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.coordinatorlayout)

    // ===== DESUGARING =====
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // ===== LIFECYCLE =====
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // ===== FIREBASE =====
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // ===== NETWORKING & JSON =====
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // ===== IMAGE LOADING =====
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // ===== COROUTINES =====
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // ===== GOOGLE PLAY SERVICES =====
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation("com.google.maps.android:android-maps-utils:3.8.0")

    // ===== MATERIAL DESIGN =====
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // ===== AR SCENEFORM =====
    implementation("com.google.ar.sceneform.ux:sceneform-ux:1.15.0")
    implementation("com.google.ar:core:1.44.0")

    // ===== AI INTEGRATION =====
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("androidx.webkit:webkit:1.11.0")

    // ===== ROOM DATABASE =====
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // ===== ACTIVITY RESULT CONTRACT =====
    implementation("androidx.activity:activity-ktx:1.8.0")

    // ===== FRAGMENT =====
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // ===== RECYCLERVIEW =====
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // ===== TESTING =====
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ===== FIX DEPENDENCY CONFLICT =====
    configurations.all {
        exclude(group = "com.android.support", module = "support-compat")
    }
}