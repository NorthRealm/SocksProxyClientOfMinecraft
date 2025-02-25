buildscript {
    apply(from = "../properties.gradle.kts")
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.squareup:javapoet:${project.findProperty("javapoet_version")}")
    implementation("com.google.code.gson:gson:${project.findProperty("gson_version")}")
    implementation("org.projectlombok:lombok:${project.extra["lombok_version"]}")
}
