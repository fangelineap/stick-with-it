import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization")
}

android {
    namespace = "com.stickwithit"
    compileSdk {
        version = release(36)
    }

    val localProperties = Properties()

    val localPropertiesFile = rootProject.file("local.properties")

    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    val ciSupabaseUrl = project.findProperty("SUPABASE_URL") as String?
    val ciSupabaseAnonKey = project.findProperty("SUPABASE_ANON_KEY") as String?
    val ciSupabaseServiceRoleKey = project.findProperty("SUPABASE_SERVICE_ROLE_KEY") as String?

    defaultConfig {
        applicationId = "com.stickwithit"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val supabaseUrl = ciSupabaseUrl ?: localProperties.getProperty("SUPABASE_URL")
        val supabaseAnonKey = ciSupabaseAnonKey ?: localProperties.getProperty("SUPABASE_ANON_KEY")
        val supabaseServiceRoleKey = ciSupabaseServiceRoleKey ?: localProperties.getProperty("SUPABASE_SERVICE_ROLE_KEY")

        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
        buildConfigField("String", "SUPABASE_SERVICE_ROLE_KEY", "\"$supabaseServiceRoleKey\"")
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
    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-text-google-fonts")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- KTOR ---
    implementation("io.ktor:ktor-client-core:3.3.3")
    implementation("io.ktor:ktor-client-content-negotiation:3.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.3")
    implementation("io.ktor:ktor-client-websockets:3.3.3")
    implementation("io.ktor:ktor-client-logging:3.3.3")
    implementation("io.ktor:ktor-client-okhttp:3.3.3")

    // --- Supabase ---
    implementation(platform("io.github.jan-tennert.supabase:bom:3.6.0"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")
    implementation("io.github.jan-tennert.supabase:functions-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")
}