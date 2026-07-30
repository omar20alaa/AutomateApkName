import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val gitBranchProvider = providers.exec {
    commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
}.standardOutput.asText.map {
    it.trim().replace("/", "_")
}

val buildDate = SimpleDateFormat("yyyy-MM-dd").format(Date())


androidComponents {

    onVariants { variant ->

        val appName = rootProject.name
        val gitBranch = gitBranchProvider.get()

        val flavor = variant.flavorName ?: "default"
        val buildType = variant.buildType ?: "debug"

        variant.outputs.forEach { output ->

            val versionName = output.versionName.orNull ?: "1.0"
            val versionCode = output.versionCode.orNull ?: 1

            output.outputFileName.set(
                "$appName-$flavor-$buildType-v$versionName($versionCode)-$gitBranch-$buildDate.apk"
            )
        }
    }
}
android {

    namespace = "app.automate_apk_name"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {

        applicationId = "app.automate_apk_name"

        minSdk = 24
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "BUILD_DATE",
            "\"$buildDate\""
        )

        buildConfigField(
            "String",
            "GIT_BRANCH",
            "\"${gitBranchProvider.get()}\""
        )
    }

    flavorDimensions += "environment"

    productFlavors {

        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }

        create("uat") {
            dimension = "environment"
            applicationIdSuffix = ".uat"
            versionNameSuffix = "-uat"
        }

        create("prod") {
            dimension = "environment"
        }
    }

    buildTypes {

        debug {
        }

        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.compose.material.icons.extended)
}