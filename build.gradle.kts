plugins {
    id("java")
}

group "com.nktfh100"
version "4.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    // Authlib -> https://papermc.io/repo/service/rest/repository/browse/maven-public/com/mojang/authlib/
    maven("https://libraries.minecraft.net/")
    maven("https://jitpack.io")
    maven("https://repo.kryptonmc.org/releases")

}

dependencies {
    // This is temporary until the plugin is rewritten...
    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.11.2")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")
    compileOnly("org.black_ixx:playerpoints:3.3.3")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.mojang:authlib:2.1.28")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.17")
    compileOnly("com.github.NEZNAMY:TAB-API:5.5.0")
    implementation("commons-io:commons-io:2.21.0")

    compileOnly(files("libs/VentureChat.jar")) // 3.8.0
    //compileOnly(files("libs/CMI-API.jar") // 9.0.0.0
}

tasks {
    compileJava {
        options.release = 17
    }
}