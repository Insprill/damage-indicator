import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta15"
    id("com.rikonardo.papermake") version "1.0.6"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17" apply false
}

group = "net.insprill"
version = this.getFullVersion()

allprojects {
    group = project.group
    version = project.version
}

// todo: Why does this need to be here?
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":nms:fallback"))
    implementation(project(":nms:v1_8_R1"))
    implementation(project(":nms:v1_8_R2"))
    implementation(project(":nms:v1_8_R3"))
    implementation(project(":nms:v1_9_R1"))
    implementation(project(":nms:v1_9_R2"))
    implementation(project(":nms:v1_10_R1"))
    implementation(project(":nms:v1_11_R1"))
    implementation(project(":nms:v1_12_R1"))
    implementation(project(":nms:v1_13_R1"))
    implementation(project(":nms:v1_13_R2"))
    implementation(project(":nms:v1_14_R1"))
    implementation(project(":nms:v1_15_R1"))
    implementation(project(":nms:v1_16_R1"))
    implementation(project(":nms:v1_16_R2"))
    implementation(project(":nms:v1_16_R3"))
    implementation(project(":nms:v1_17_R0"))
    implementation(project(":nms:v1_17_R1", "reobf"))
    implementation(project(":nms:v1_18_R1", "reobf"))
    implementation(project(":nms:v1_18_R2", "reobf"))
    implementation(project(":nms:v1_19_R1", "reobf"))
    implementation(project(":nms:v1_19_R2", "reobf"))
    implementation(project(":nms:v1_19_R3", "reobf"))
    implementation(project(":nms:v1_20_R1", "reobf"))
    implementation(project(":nms:v1_20_R2", "reobf"))
    implementation(project(":nms:v1_20_R3", "reobf"))
    implementation(project(":nms:v1_20_Z5_6", "reobf"))
    implementation(project(":nms:v1_21_0-2", "reobf"))
    implementation(project(":nms:v1_21_3-4", "reobf"))
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("")

        val libPackage = "com.zenya.damageindicator.libs"
        relocate("net.insprill.xenlib", "${libPackage}.net.insprill.xenlib")
        relocate("net.insprill.spigotutils", "${libPackage}.net.insprill.spigotutils")
        relocate("org.bstats", "${libPackage}.org.bstats")
    }

    build {
        dependsOn(shadowJar)
    }
}

fun getFullVersion(): String {
    val version = project.property("version")!! as String
    return if (version.contains("-SNAPSHOT")) {
        "$version+rev.${getGitHash()}"
    } else {
        version
    }
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--verify", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}
