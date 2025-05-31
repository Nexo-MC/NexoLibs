//import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    id("java")
    id("maven-publish")
    alias(idofrontLibs.plugins.mia.kotlin.jvm)
    alias(idofrontLibs.plugins.mia.papermc)
    alias(idofrontLibs.plugins.mia.copyjar)
    alias(idofrontLibs.plugins.mia.autoversion)
    id("xyz.jpenilla.run-paper") version "2.3.1" // Adds runServer and runMojangMappedServer tasks for testing
    //id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
}

val commandApiVersion = "10.0.1"
val googleGsonVersion = "2.11.0"
val idofrontVersion: String by project
val apacheLang3Version = "3.17.0"
val apacheHttpClientVersion = "5.4.3"
val creativeVersion = "1.8.5-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://papermc.io/repo/repository/maven-public/") // Paper
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceHolderAPI
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/") // CustomBlockData
    maven("https://repo.triumphteam.dev/snapshots") // actions-code, actions-spigot
    maven("https://mvn.lumine.io/repository/maven-public/") { metadataSources { artifact() } }// MythicMobs
    maven("https://repo.nexomc.com/releases")
    maven("https://repo.nexomc.com/snapshots")
    maven("https://repo.mineinabyss.com/releases")
    maven("https://repo.mineinabyss.com/snapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots") // commandAPI snapshots
    maven("https://repo.auxilor.io/repository/maven-public/") // EcoItems
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.unnamed.team/repository/unnamed-public/") // Creative
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/") // MMOItems
    maven("https://repo.codemc.org/repository/maven-public/") // BlockLocker

    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")

    api("team.unnamed:creative-api:$creativeVersion") { exclude("net.kyori") }
    api("team.unnamed:creative-serializer-minecraft:$creativeVersion") { exclude("net.kyori") }
    api("team.unnamed:creative-server:$creativeVersion")

    api("dev.jorel:commandapi-bukkit-shade-mojang-mapped:$commandApiVersion")
    api("dev.jorel:commandapi-bukkit-kotlin:$commandApiVersion")
    api("org.spongepowered:configurate-yaml:4.1.2")
    api("org.spongepowered:configurate-extra-kotlin:4.1.2")
    api("com.github.stefvanschie.inventoryframework:IF:0.10.19")
    api("com.jeff-media:custom-block-data:2.2.2")
    api("com.jeff-media:MorePersistentDataTypes:2.4.0")
    api("com.jeff-media:persistent-data-serializer:1.0")
    api("dev.triumphteam:triumph-gui:3.2.0-SNAPSHOT") { exclude("net.kyori") }
    api("gs.mclo:java:2.2.1")

    api("commons-io:commons-io:2.18.0")
    api("com.google.code.gson:gson:$googleGsonVersion")
    api("org.apache.commons:commons-lang3:$apacheLang3Version")
    api("org.apache.httpcomponents.client5:httpclient5:$apacheHttpClientVersion")
    api("org.springframework:spring-expression:6.1.14")
    api("org.jetbrains:annotations:26.0.1") { isTransitive = false }
}

tasks {
    shadowJar {
        //relocate("kotlinx.", "com.nexomc.libs.kotlinx.")
        //relocate("kotlin.", "com.nexomc.libs.kotlin.")
        fun shade(groupId: String) = relocate(groupId, "com.nexomc.libs")
        //shade("dev.jorel")
        shade("com.jeff_media")
        shade("com.github.stefvanschie")
        shade("org.spongepowered")
        shade("dev.triumphteam")
    }
}

copyJar {
    destPath.set(project.findProperty("nexo_plugin_path").toString())
    val version: String by project
    jarName.set("NexoLibs-$version.lib")
    excludePlatformDependencies.set(false)
}

publishing {
    repositories {
        maven {
            val repo = "https://repo.nexomc.com/"
            val isSnapshot = System.getenv("IS_SNAPSHOT") == "true"
            val url = if (isSnapshot) repo + "snapshots" else repo + "releases"
            setUrl(url)
            credentials {
                username = project.findProperty("mineinabyssMavenUsername") as String?
                password = project.findProperty("mineinabyssMavenPassword") as String?
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

//bukkitPluginYaml {
//    main = "com.nexomc.nexo.NexoLibs"
//    name = "NexoLibs"
//    apiVersion = "1.20"
//    val version: String by project
//    this.version = version
//    authors.add("boy0000")
//    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
//}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-opt-in=org.jetbrains.annotations.ApiStatus.Experimental",
            "-opt-in=kotlin.ExperimentalUnsignedTypes",
            "-Xcontext-receivers"
        )
    }
}
