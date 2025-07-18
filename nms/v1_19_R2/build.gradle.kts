plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

dependencies {
    compileOnly(project(":core"))
    paperweight.paperDevBundle("1.19.3-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
