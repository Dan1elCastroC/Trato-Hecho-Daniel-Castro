plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    jacoco
}

android {
    namespace = "com.example.tratohecho"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tratohecho"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // Firma del APK release: el .jks va en la carpeta keystore/ del repositorio (requisito de la evaluacion).
    signingConfigs {
        create("release") {
            storeFile = rootProject.file("keystore/tratohecho.jks")
            storePassword = "tratohecho123"
            keyAlias = "tratohecho"
            keyPassword = "tratohecho123"
        }
    }

    buildTypes {
        debug { enableUnitTestCoverage = true }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Pruebas unitarias (Guia 15): Kotest + JUnit5 + MockK + corrutinas
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.json:json:20231013") // org.json real para JVM (en Android viene incluido)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // Prueba de interfaz con Compose UI Test
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    debugImplementation("androidx.compose.ui:ui-tooling")
}

// Obligatorio para usar JUnit 5 / Kotest
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Cobertura de la logica (ViewModel, Repository, validadores, API). Ejecutar: gradlew :app:cobertura
tasks.register<JacocoReport>("cobertura") {
    dependsOn("testDebugUnitTest")
    val clases = fileTree("$buildDir/tmp/kotlin-classes/debug") {
        include(
            "com/example/tratohecho/data/**", "com/example/tratohecho/viewmodel/**",
            "com/example/tratohecho/model/**", "com/example/tratohecho/ui/components/CalculadoraCotizacion*"
        )
        exclude("**/AppDatabase*", "**/*Dao_Impl*", "**/*\$*Companion*", "**/ComposableSingletons*")
    }
    classDirectories.setFrom(clases)
    sourceDirectories.setFrom("src/main/java")
    executionData.setFrom(fileTree(buildDir) { include("**/*.exec", "**/*.ec") })
    reports { xml.required.set(true); html.required.set(true); csv.required.set(true) }
}
