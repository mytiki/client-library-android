// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "8.2.2" apply false
  id("org.jetbrains.kotlin.android") version "1.9.22" apply false
  id("com.android.library") version "8.2.2" apply false
  id("maven-publish")
  id("org.jetbrains.dokka") version "1.8.10"
}

group = "com.mytiki"

version = "1.0.0"
