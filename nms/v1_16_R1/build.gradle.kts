plugins {
    id("java")
}

repositories {
    mavenLocal()
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("org.spigotmc:spigot:1.16.1-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
