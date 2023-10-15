/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val aboutLibrariesVersion by extra("10.9.1")
    val composeVersion by extra("1.6.0-alpha07")
    val wearComposeVersion by extra("1.3.0-alpha07")

    repositories {
        google()
        mavenCentral()
        //NEEDED FOR TOOLTIPS
        maven { url = uri("https://jitpack.io") }
        //NEEDED FOR ABOUTLIBRARIES
        maven { url = uri("https://plugins.gradle.org/m2/")}
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")

        //ABOUT LIBRARIES
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:$aboutLibrariesVersion")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://jitpack.io") }
    }
}

plugins {
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
}