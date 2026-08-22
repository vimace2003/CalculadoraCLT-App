plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.calculadoraclt.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.calculadoraclt.app"
        minSdk = 26
        targetSdk = 36
        versionCode = (System.getenv("ANDROID_VERSION_CODE")?.toIntOrNull()) ?: 1
        versionName = System.getenv("ANDROID_VERSION_NAME") ?: "1.0.0"

        // App ID de TESTE do AdMob por padrão (debug, CI, etc.) — o build de release
        // sobrescreve com o App ID real (ver buildTypes.release abaixo).
        manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
    }

    val hasCiSigningConfig = System.getenv("CI_KEYSTORE_PATH") != null

    signingConfigs {
        if (hasCiSigningConfig) {
            create("release") {
                storeFile = file(System.getenv("CI_KEYSTORE_PATH")!!)
                storePassword = System.getenv("CI_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("CI_KEY_ALIAS")
                keyPassword = System.getenv("CI_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (hasCiSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
            manifestPlaceholders["admobAppId"] = "ca-app-pub-6996977326182038~2706116970"
            // Empacota os simbolos de depuracao nativos (de bibliotecas .so de terceiros,
            // ex: Play Services/AdMob) direto no AAB, sem precisar de upload manual no Console.
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.play.services.ads)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)
}
