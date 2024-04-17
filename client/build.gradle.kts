plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka")
    id("maven-publish")
    id("signing")
}

.0.2"

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
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{NOTICE,LICENSE,DEPENDENCIES,LICENSE.md,NOTICE.txt,NOTICE.md}"
        }
    }
}

dependencies {

    // Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.2")

    // Okhttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Bouncy Castle
    implementation("org.bouncycastle:bcpkix-jdk15to18:1.68")
    implementation("org.bouncycastle:bcprov-jdk15to18:1.68")

    // Test
    testImplementation("junit:junit:4.13.2")
}

tasks.dokkaHtml {
    outputDirectory.set(file("../docs"))
}

signing {
    val signingKey = System.getenv("PGP_PRIVATE_KEY")
    val signingPassword = System.getenv("PGP_PASSPHRASE")
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
                    description.set("A package for adding TIKI's decentralized infrastructure to Android projects. Add tokenized data ownership, consent, and rewards to your app in minutes.")
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
                    username = System.getenv("OSSRH_USER")
                    password = System.getenv("OSSRH_TOKEN")
                }
            }
        }
    }
}
