plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

dependencies {
    compileOnly(project(":core"))
    paperweight.paperDevBundle("1.17.1-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}
