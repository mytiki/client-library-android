plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mytiki.publish.client"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        manifestPlaceholders["appAuthRedirectScheme"] = "com.googleusercontent.apps.1069507745611-ccoh4pjgfpoqf5t2p2poanuqiovtnjun"
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
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

}

dependencies {


    val composeBom = platform("androidx.compose:compose-bom:2023.03.00")

//  Tiki
    implementation("com.mytiki:tiki-sdk-android:2.2.1")

//    Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

//    AppAuth
    implementation ("net.openid:appauth:0.11.1")

//    Okhttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("io.nem:api-okhttp-gson-client:0.8.2")

    //Jetpack Compose
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(composeBom)

//  Test
    androidTestImplementation(composeBom)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20230227")
    testImplementation ("io.mockk:mockk:1.13.9")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("io.mockk:mockk-android:1.13.9")
}