plugins {
    kotlin("jvm") version "2.2.20"
    id("org.jetbrains.kotlin.plugin.spring") version "2.2.20"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.6"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
    }
}

repositories {
    mavenCentral()
    maven {
        name = "Spring Milestones"
        url = uri("https://repo.spring.io/milestone")
    }
}

springBoot {
    mainClass = "jakoboeu.ApplicationKt"
}

dependencies {
    implementation(platform("org.springframework.ai:spring-ai-bom:1.0.3"))
    implementation("org.springframework.ai:spring-ai-starter-model-openai")

    implementation(platform("com.fasterxml.jackson:jackson-bom:2.20.0"))
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation(kotlin("test"))
}

tasks.withType<JavaCompile> {
    options.release.set(24)
}

tasks.test { useJUnitPlatform() }

