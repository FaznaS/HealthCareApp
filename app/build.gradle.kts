plugins {
    id("com.android.application")
}

android {
    namespace = "com.s22010466.healthcare"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.s22010466.healthcare"
        minSdk = 21
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.android.material:material:1.3.0-alpha03")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    // play services library required for activity recognition.
    implementation ("com.google.android.gms:play-services-location:21.2.0")
    // Picasso Library
    implementation("com.squareup.picasso:picasso:2.8")
    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    //Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    //Work Manager
    implementation("androidx.work:work-runtime:2.9.0")
}