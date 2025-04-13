plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
}

dependencies {
    compileOnly(project(":core"))
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
