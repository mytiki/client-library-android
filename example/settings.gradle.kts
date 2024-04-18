pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    maven {
      name = "local"
      url = uri("../client/build/repo")
    }
    google()
    mavenCentral()
  }
}

rootProject.name = "example"

include(":app")
