import org.gradle.api.JavaVersion

object Libraries {
    const val kotlin_std_lib_java8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"

    object Versions {
        const val kotlin = "1.7.22"
    }
}

object BuildConfig {
    const val minSdk = 26
    const val compileSdk = 33
    const val targetSdk = 33
    val javaVersion = JavaVersion.VERSION_1_8
}
