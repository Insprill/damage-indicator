plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.3.8"
}

dependencies {
    compileOnly(project(":core"))
    paperDevBundle("1.19.2-R0.1-SNAPSHOT")
}

tasks {
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
