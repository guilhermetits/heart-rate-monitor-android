@file:Suppress("unused")

import org.gradle.api.JavaVersion

object Libraries {
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
    const val androidXKtx = "androidx.core:core-ktx:1.9.0"
    const val androidXLifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1"
    const val androidXTestJunit = "androidx.test.ext:junit:1.1.4"
    const val androidXTestEspresso = "androidx.test.espresso:espresso-core:3.5.0"

    const val koinCore = "io.insert-koin:koin-core:3.3.2"
    const val koinAndroid = "io.insert-koin:koin-android:3.3.2"
    const val koinCompose = "io.insert-koin:koin-androidx-compose:3.4.1"

    const val composeBom = "androidx.compose:compose-bom:2022.12.00"
    const val composeActivity = "androidx.activity:activity-compose:1.6.1"
    const val composeUi = "androidx.compose.ui:ui"
    const val composeUiPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeMaterial3 = "androidx.compose.material3:material3"
    const val composeJunit = "androidx.compose.ui:ui-test-junit4"
    const val composeDebugUiTooling = "androidx.compose.ui:ui-tooling"
    const val composeTestManifest = "androidx.compose.ui:ui-test-manifest"

    const val nordicBleKtx = "no.nordicsemi.android:ble-ktx:2.4.0"
    const val timber = "com.jakewharton.timber:timber:5.0.1"

    const val junit = "junit:junit:4.13.2"
}

object BuildConfig {
    const val minSdk = 26
    const val compileSdk = 33
    const val targetSdk = 33
    val javaVersion = JavaVersion.VERSION_1_8
    const val kotlinCompilerExtensionVersion = "1.3.2"
}
