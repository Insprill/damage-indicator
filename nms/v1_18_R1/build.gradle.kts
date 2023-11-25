plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.5.10"
}

dependencies {
    compileOnly(project(":core"))
    paperweight.paperDevBundle("1.18-R0.1-SNAPSHOT")
}

tasks {
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
