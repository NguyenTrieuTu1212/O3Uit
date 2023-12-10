plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.o3uit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.o3uit"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.mapbox.maps:android:10.12.1") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation ("com.mapbox.plugin:maps-lifecycle:10.13.0-beta.1") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation ("com.mapbox.plugin:maps-scalebar:10.13.0-beta.1") {
        exclude(group = "org.jetbrains.kotlin")
    }


    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.17")
}