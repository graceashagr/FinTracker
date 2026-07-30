plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.junit5.android)
}

android {
    namespace = "com.example.core.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 26

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {

    // project module dependency
    implementation(project(":core:domain"))

    // firebase firestore
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    // room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // dagger - hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // kotlin coroutine
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.work.runtime.ktx)

    // Hilt worker
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler.androidx)

    // test
    testImplementation(libs.junit5.jupiter)
    testRuntimeOnly(libs.junit5.jupiter.engine)
    testRuntimeOnly(libs.junit5.platform.launcher)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    testImplementation(project(":core:testing"))
}