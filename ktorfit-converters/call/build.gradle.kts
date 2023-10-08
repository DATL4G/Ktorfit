plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
    id("com.android.library")
    alias(libs.plugins.binaryCompatibilityValidator)
    id("app.cash.licensee")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}


val enableSigning = project.hasProperty("signingInMemoryKey")

mavenPublishing {

    coordinates(
        "de.jensklingenberg.ktorfit",
        "ktorfit-converters-call",
        libs.versions.ktorfit.get()
    )
    publishToMavenCentral()
    // publishToMavenCentral(SonatypeHost.S01) for publishing through s01.oss.sonatype.org
    if (enableSigning) {
        signAllPublications()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


kotlin {
    explicitApi()
    jvm {

    }
    js(IR) {
        this.nodejs()
        binaries.executable() // not applicable to BOTH, see details below
    }
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    macosX64()
    macosArm64()
    linuxX64 {
        binaries {
            executable()
        }
    }

    ios("ios") {
        binaries {
            framework {
                baseName = "library"
            }
        }
    }
    mingwX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.ktorfitLibCommon)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val linuxX64Main by getting
        val mingwX64Main by getting
        val androidMain by getting
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(libs.ktor.client.mock)
                implementation(libs.mockk)
                implementation(libs.mockito.kotlin)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val jsMain by getting
        val iosMain by getting {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {

            }
        }
    }
}
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
    namespace = "de.jensklingenberg.ktorfit.converters.call"
}



publishing {
    publications {
        create<MavenPublication>("default") {
            artifact(tasks["sourcesJar"])
            // artifact(tasks["javadocJar"])

            pom {
                name.set(project.name)
                description.set("Call Converter for Ktorfit")
                url.set("https://github.com/Foso/Ktorfit")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/Foso/Ktorfit/blob/master/LICENSE.txt")
                    }
                }
                scm {
                    url.set("https://github.com/Foso/Ktorfit")
                    connection.set("scm:git:git://github.com/Foso/Ktorfit.git")
                }
                developers {
                    developer {
                        name.set("Jens Klingenberg")
                        url.set("https://github.com/Foso")
                    }
                }
            }
        }
    }

    repositories {
        if (
            hasProperty("sonatypeUsername") &&
            hasProperty("sonatypePassword") &&
            hasProperty("sonatypeSnapshotUrl") &&
            hasProperty("sonatypeReleaseUrl")
        ) {
            maven {
                val url = when {
                    "SNAPSHOT" in version.toString() -> property("sonatypeSnapshotUrl")
                    else -> property("sonatypeReleaseUrl")
                } as String
                setUrl(url)
                credentials {
                    username = property("sonatypeUsername") as String
                    password = property("sonatypePassword") as String
                }
            }
        }

    }
}

rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class) {
    rootProject.the(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension::class).nodeVersion = "18.0.0"
}