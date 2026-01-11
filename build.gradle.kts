plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "dev.kaato"
version = "2.1.3"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://maven.elmakers.com/repository/") {
        name = "elmakers-repo"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("dev.kaato:NotzAPI:0.4.8.1")
    implementation("org.bstats:bstats-bukkit:3.1.0")
}

val targetJavaVersion = 8
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

//tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
//    configurations = listOf(project.configurations.runtimeClasspath.get())
//    relocate("org.bstats", project.group.toString())
//}

tasks.jar {
    archiveFileName.set("NotzWarps-${project.version}.jar")
//    destinationDirectory.set(file("G:/Gago32/Minecraft/Servidor/Rede Notz/1.18-1.21/plugins"))
}