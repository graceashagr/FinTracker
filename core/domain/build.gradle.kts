plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    testImplementation(libs.junit)
}