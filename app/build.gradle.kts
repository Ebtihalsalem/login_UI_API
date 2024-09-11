plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.example.login_ui_api"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.login_ui_api"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

        //implementation (androidx.compose.material3:material3:1.0.0)
        //implementation (androidx.compose.ui:ui:1.4.0)


    val voyagerVersion = "1.1.0-beta02"

    // Multiplatform
    // Navigator
    implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")

    // Transitions (optional)
    implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")

    val  ktor_version = "2.2.3"
    implementation ("io.ktor:ktor-client-core:$ktor_version")
    implementation ("io.ktor:ktor-client-android:$ktor_version")
    implementation ("io.ktor:ktor-client-serialization:$ktor_version")
    implementation ("io.ktor:ktor-client-logging:$ktor_version")
    implementation ("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    val lifecycle_version = "2.8.5"

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")

    // for loading the image (optional)
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    //latest version: https://github.com/adrielcafe/voyager/releases


    //implementation( "androidx.security:security-crypto:1.1.0-alpha03")

//        implementation (androidx.compose.ui:ui:1.0.5)
//        implementation (androidx.compose.material:material:1.0.5)
//        implementation (androidx.compose.ui:ui-tooling-preview:1.0.5)
//        implementation (com.squareup.okhttp3:okhttp:4.9.1)
    //latest version: https://github.com/adrielcafe/voyager/releases
    val voyagerVersion2 = "1.1.0-beta02"

    // Multiplatform
    // Navigator
    implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion2")

    // Transitions (optional)
    implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion2")
    implementation ("androidx.navigation:navigation-compose:2.5.3")

}