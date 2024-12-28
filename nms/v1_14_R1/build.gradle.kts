plugins {
    id("java")
}

repositories {
    mavenLocal()
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("org.spigotmc:spigot:1.14.4-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
