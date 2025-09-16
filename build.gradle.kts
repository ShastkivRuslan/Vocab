plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.dagger.hilt.android") version "2.52" apply false
    alias(libs.plugins.ksp) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
}