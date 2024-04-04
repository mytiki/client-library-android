plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.mytiki.publish.client"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{NOTICE,LICENSE,DEPENDENCIES,LICENSE.md,NOTICE.txt,NOTICE.md}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {

    val material3Version = "1.2.0"
    val composeBom = platform("androidx.compose:compose-bom:2023.03.00")


//    Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.2")

    //Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.7")


//    Okhttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    //Jetpack Compose
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation(composeBom)
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

//    Coil
    implementation("io.coil-kt:coil-compose:2.2.2")

//    Bouncy Castle
    implementation("org.bouncycastle:bcpkix-jdk15to18:1.68")
    implementation("org.bouncycastle:bcprov-jdk15to18:1.68")

//  Test
    androidTestImplementation(composeBom)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20230227")
    testImplementation ("io.mockk:mockk:1.13.9")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("io.mockk:mockk-android:1.13.9")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.android.support.test:rules:1.0.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.2")
    debugImplementation("androidx.test:core:1.5.0")
    debugImplementation("androidx.test:rules:1.5.0")
    debugImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("io.mockk:mockk-agent:1.13.9")
    testImplementation("io.mockk:mockk-android:1.13.9")
    testImplementation("io.mockk:mockk-agent:1.13.9")
}
