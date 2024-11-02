plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.4"
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
