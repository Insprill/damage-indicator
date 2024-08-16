plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.2"
}

dependencies {
    compileOnly(project(":core"))
    paperweight.paperDevBundle("1.19.2-R0.1-SNAPSHOT")
}

tasks {
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
