plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

dependencies {
    compileOnly(project(":core"))
    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
