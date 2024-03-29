import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(from="./src/main/resources/version.gradle")

plugins {
    kotlin("jvm") version "1.6.20"
    application
    id("io.gitlab.arturbosch.detekt").version("1.22.0")
}

group = "de.kantagonist"
version = rootProject.ext.properties["mainVersion"]!!

repositories {
    mavenCentral()
}

dependencies {

    // Testing
    testImplementation(kotlin("test"))

    // Yaml resolvers
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-yaml
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Jar> {

    // creates manifest
    archiveFileName.set("xdst.jar")
    manifest {
        attributes["Main-Class"] = "MainKt"
    }

    // adds all dependencies for fat jar creation
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

application {
    mainClass.set("MainKt")
}