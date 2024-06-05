import java.io.FileNotFoundException
import java.util.*

plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.dokka")
  id("maven-publish")
  id("signing")
}

val versionName = "0.6.0"
val localProps = Properties()

try {
  localProps.load(File("local.properties").reader())
} catch (e: FileNotFoundException) {
  println("local.properties file not found. Using env secrets.")
}

android {
  namespace = "com.mytiki.publish.client"
  compileSdk = 34

  defaultConfig {
    minSdk = 26
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
    vectorDrawables { useSupportLibrary = true }
    manifestPlaceholders["appAuthRedirectScheme"] =
        "com.googleusercontent.apps.1079849396355-pcadmpajhn1tpmm2augu633cdvbu68k9"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
  buildFeatures {
    compose = true
    viewBinding = true
  }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }

  packaging {
    resources {
      excludes += "/META-INF/{NOTICE,LICENSE,DEPENDENCIES,LICENSE.md,NOTICE.txt,NOTICE.md}"
    }
  }
}

dependencies {

  // Android
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
  implementation("androidx.security:security-crypto:1.1.0-alpha06")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")

  // Coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

  // AppAuth
  implementation("net.openid:appauth:0.11.1")

  // Okhttp
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

  // Bouncy Castle
  implementation("org.bouncycastle:bcpkix-jdk15to18:1.78")

  // Compose
  implementation(platform("androidx.compose:compose-bom:2023.03.00"))
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.compose.ui:ui-android:1.6.7")
  implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
  implementation("androidx.navigation:navigation-compose:2.7.7")
  implementation("androidx.compose.material3:material3:1.2.1")
  // Test
  implementation("androidx.test:monitor:1.6.1")
  testImplementation("junit:junit:4.13.2")
  testImplementation("io.mockk:mockk:1.13.9")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
  testImplementation("io.mockk:mockk-agent:1.13.9")
  androidTestImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test:runner:1.5.2")
  androidTestImplementation("androidx.test:core:1.5.0")
  androidTestImplementation("androidx.test:rules:1.5.0")
}

tasks.dokkaHtml { outputDirectory.set(file("../docs")) }

signing {
  val signingKey = System.getenv("PGP_PRIVATE_KEY") ?: localProps.getProperty("PGP_PRIVATE_KEY")
  val signingPassword = System.getenv("PGP_PASSPHRASE") ?: localProps.getProperty("PGP_PASSPHRASE")
  useInMemoryPgpKeys(signingKey, signingPassword)
  sign(publishing.publications)
}

afterEvaluate {
  publishing {
    publications {
      register("release", MavenPublication::class) {
        from(components["release"])
        groupId = "com.mytiki"
        artifactId = "publish-client"
        version = versionName

        pom {
          name.set("TIKI Publish Client [Android]")
          description.set(
              "A package for adding TIKI's decentralized infrastructure to Android projects. Add tokenized data ownership, consent, and rewards to your app in minutes.")
          url.set("https://docs.mytiki.com/reference/client-library-overview")

          licenses {
            license {
              name.set("MIT")
              url.set("https://github.com/tiki/tiki-sdk-android/blob/main/LICENSE")
            }
          }

          developers {
            developer {
              name.set("The TIKI Team")
              email.set("hello@mytiki.com")
              organization.set("TIKI")
              organizationUrl.set("https://mytiki.com")
            }
          }

          scm {
            url.set("https://github.com/tiki/publish-client-android")
            tag.set(versionName)
          }
        }
      }
    }

    repositories {
      maven {
        name = "localRepo"
        setUrl(layout.buildDirectory.dir("repo"))
      }

      maven {
        name = "OSSRH"
        url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        credentials {
          username = System.getenv("OSSRH_USER") ?: localProps.getProperty("OSSRH_USER")
          password = System.getenv("OSSRH_TOKEN") ?: localProps.getProperty("OSSRH_TOKEN")
        }
      }
    }
  }
}
