plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.3.11"
}

dependencies {
    compileOnly(project(":core"))
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
}

tasks {
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
