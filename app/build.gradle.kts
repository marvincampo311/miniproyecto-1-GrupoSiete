// Plugin configuration for Android application and Kotlin support
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // Enables Kotlin Annotation Processing
}

android {
    namespace = "com.example.miiproyecto1" // Application package namespace
    compileSdk = 36 // Android API level used to compile the app

    defaultConfig {
        applicationId = "com.example.miiproyecto1" // Unique application ID
        minSdk = 24 // Minimum supported Android version
        targetSdk = 36 // Target Android version for compatibility
        versionCode = 1 // Internal app version
        versionName = "1.0" // User-facing version name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Test runner configuration
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Disables code shrinking/obfuscation for release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), // Default ProGuard rules
                "proguard-rules.pro" // Custom ProGuard rules
            )
        }
    }

    buildFeatures {
        viewBinding = true // Enables ViewBinding for safer UI access
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Code shrinking disabled (duplicate block — possibly intentional)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Java language compatibility
        targetCompatibility = JavaVersion.VERSION_11 // JVM bytecode target version
    }

    kotlinOptions {
        jvmTarget = "11" // Kotlin JVM target version
    }
}

dependencies {

    // Core Android and Jetpack libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Additional specific dependencies
    implementation("androidx.core:core-ktx:1.10.1") // Kotlin extensions
    implementation("androidx.biometric:biometric:1.1.0") // Biometric authentication support
    implementation("com.airbnb.android:lottie:6.0.0") // Lottie animations
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // ConstraintLayout UI system
    implementation("androidx.cardview:cardview:1.0.0") // CardView UI component

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0") // Kotlin standard library

    // Room Database (Persistence)
    implementation("androidx.concurrent:concurrent-futures:1.1.0") // Concurrency utilities
    implementation("com.google.guava:guava:31.0.1-android") // Utility library collection
    annotationProcessor("org.projectlombok:lombok:1.18.30") // Lombok annotation processing
    annotationProcessor("org.projectlombok:lombok:1.18.30") // Duplicate (kept as requested)
    implementation("androidx.room:room-runtime:2.6.1") // Room runtime
    kapt("androidx.room:room-compiler:2.6.1") // Room annotation processor
}
