/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        maven { url = uri("https://jitpack.io/") }
        mavenCentral()
    }
}
rootProject.name = "TSViewer"
include (":app")
include (":wear")
