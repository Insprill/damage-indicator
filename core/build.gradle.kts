plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    implementation("net.insprill:spigot-utils:0.2.1")
    implementation("net.insprill:XenLib:d7d95983fe")
    implementation("org.bstats:bstats-bukkit:3.0.0")
}

tasks {
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }
}
