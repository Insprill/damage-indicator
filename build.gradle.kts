plugins {
    id("java")
    id("org.ajoberstar.grgit") version "5.3.3"
    id("com.gradleup.shadow") version "9.2.2"
    id("com.rikonardo.papermake") version "1.0.6"
    id("com.modrinth.minotaur") version "2.8.10"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19" apply false
}

group = "net.insprill"
version = "${project.version}${versionMetadata()}"

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
    implementation(project(":nms:v1_21_3-8", "reobf"))
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

val minecraftVersions = arrayOf(
    "1.8",
    "1.8.1",
    "1.8.2",
    "1.8.3",
    "1.8.4",
    "1.8.5",
    "1.8.6",
    "1.8.7",
    "1.8.8",
    "1.8.9",
    "1.9",
    "1.9.1",
    "1.9.2",
    "1.9.3",
    "1.9.4",
    "1.10",
    "1.10.1",
    "1.10.2",
    "1.11",
    "1.11.1",
    "1.11.2",
    "1.12",
    "1.12.1",
    "1.12.2",
    "1.13",
    "1.13.1",
    "1.13.2",
    "1.14",
    "1.14.1",
    "1.14.2",
    "1.14.3",
    "1.14.4",
    "1.15",
    "1.15.1",
    "1.15.2",
    "1.16",
    "1.16.1",
    "1.16.2",
    "1.16.3",
    "1.16.4",
    "1.16.5",
    "1.17",
    "1.17.1",
    "1.18",
    "1.18.1",
    "1.18.2",
    "1.19",
    "1.19.1",
    "1.19.2",
    "1.19.3",
    "1.19.4",
    "1.20",
    "1.20.1",
    "1.20.2",
    "1.20.3",
    "1.20.4",
    "1.20.5",
    "1.20.6",
    "1.21",
    "1.21.1",
    "1.21.2",
    "1.21.3",
    "1.21.4",
    "1.21.5",
    "1.21.6",
    "1.21.7",
    "1.21.8"
)

modrinth {
    changelog.set(readChangelog(project.version as String))
    token.set(System.getenv("MODRINTH_API_TOKEN") ?: findProperty("modrinthToken") as String?)
    projectId.set(property("modrinth.project.id") as String)
    versionType.set(if ((findProperty("build.is-release") as String? ?: "true").toBoolean()) "release" else "alpha")
    uploadFile.set(tasks.shadowJar.get())
    loaders.addAll("spigot", "paper", "purpur")
    syncBodyFrom.set(file("modrinth_page.md").readText())
    gameVersions.addAll(*minecraftVersions)
}

fun readChangelog(version: String): String {
    val lines = file("CHANGELOG.md").readLines()
    val out = StringBuilder()
    var inVersion = false
    for (line in lines) {
        if (line.startsWith("## [$version] - ")) {
            inVersion = true
            continue
        }
        if (inVersion) {
            if (line.startsWith("## ")) {
                break
            }
            out.append(line).append("\n")
        }
    }

    if (out.isBlank()) {
        out.append("[${grgit.head().abbreviatedId}](${grgit.remote.list().first().url}/commit/${grgit.head().id}): ${grgit.head().fullMessage}")
    }

    return out.toString().trim()
}

fun versionMetadata(): String {
    if (!property("version.metadata").toString().toBoolean()) {
        return ""
    }

    val head = grgit.head()
    var id = head.abbreviatedId

    if (!grgit.status().isClean) {
        id += ".dirty"
    }

    return "+rev.${id}"
}

