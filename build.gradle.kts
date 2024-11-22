import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    id("java")
    alias(idofrontLibs.plugins.mia.kotlin.jvm)
    alias(idofrontLibs.plugins.mia.papermc)
    alias(idofrontLibs.plugins.mia.copyjar)
    alias(idofrontLibs.plugins.mia.publication)
    alias(idofrontLibs.plugins.mia.autoversion)
    id("xyz.jpenilla.run-paper") version "2.3.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
    id("io.papermc.paperweight.userdev") version "1.7.5" apply false
    id("com.gradleup.shadow") version "8.3.5"
}

val commandApiVersion = "9.6.1"
val adventureVersion = "4.17.0"
val platformVersion = "4.3.4"
val googleGsonVersion = "2.11.0"
val apacheLang3Version = "3.17.0"
val apacheHttpClientVersion = "5.4"
val creativeVersion = "1.7.3"

repositories {
    mavenCentral()

    maven("https://papermc.io/repo/repository/maven-public/") // Paper
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://oss.sonatype.org/content/repositories/snapshots") // Because Spigot depends on Bungeecord ChatComponent-API
    maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven("https://libraries.minecraft.net/") // Minecraft repo (commodore)
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceHolderAPI
    maven("https://maven.elmakers.com/repository/") // EffectLib
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/") // CustomBlockData
    maven("https://repo.triumphteam.dev/snapshots") // actions-code, actions-spigot
    maven("https://mvn.lumine.io/repository/maven-public/") { metadataSources { artifact() } }// MythicMobs
    maven("https://repo.mineinabyss.com/releases")
    maven("https://repo.mineinabyss.com/snapshots")
    maven("https://repo.oraxen.com/releases")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots") // commandAPI snapshots
    maven("https://repo.auxilor.io/repository/maven-public/") // EcoItems
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io") // JitPack
    maven("https://repo.unnamed.team/repository/unnamed-public/") // Creative
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/") // MMOItems
    maven("https://repo.codemc.org/repository/maven-public/") // BlockLocker

    mavenLocal()
}

dependencies {
    api(idofrontLibs.kotlin.stdlib)

    api("net.kyori:adventure-text-minimessage:$adventureVersion")
    api("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    api("net.kyori:adventure-text-serializer-ansi:$adventureVersion")
    api("net.kyori:adventure-platform-bukkit:$platformVersion")

    api("team.unnamed:creative-api:$creativeVersion") { exclude("net.kyori") }
    api("team.unnamed:creative-server:$creativeVersion")
    api("team.unnamed:creative-serializer-minecraft:1.7.4-SNAPSHOT") { exclude("net.kyori") }

    api("dev.jorel:commandapi-bukkit-shade:$commandApiVersion")
    api("dev.jorel:commandapi-bukkit-kotlin:$commandApiVersion")

    api("org.bstats:bstats-bukkit:3.1.0")
    api("org.glassfish:javax.json:1.1.4")
    api("io.th0rgal:protectionlib:1.6.2")
    api("org.springframework:spring-expression:6.0.8")
    api("org.apache.commons:commons-lang3:$apacheLang3Version")
    api("org.apache.httpcomponents.client5:httpclient5:$apacheHttpClientVersion")
    api("com.google.code.gson:gson:$googleGsonVersion")
    api("com.github.stefvanschie.inventoryframework:IF:0.10.12")
    api("com.jeff-media:custom-block-data:2.2.2")
    api("com.jeff-media:MorePersistentDataTypes:2.4.0")
    api("com.jeff-media:persistent-data-serializer:1.0")
    api("org.jetbrains:annotations:26.0.1") { isTransitive = false }
    api("dev.triumphteam:triumph-gui:3.1.10") { exclude("net.kyori") }
    api("gs.mclo:java:2.2.1")

    api("com.github.technicallycoded:FoliaLib:main-SNAPSHOT")
    api("org.spongepowered:configurate-yaml:4.1.2")
    api("org.spongepowered:configurate-extra-kotlin:4.1.2")

    api("me.gabytm.util:actions-spigot:1.0.0-SNAPSHOT") { exclude(group = "com.google.guava") }
}

tasks {
    shadowJar {
        relocate("com.github.technicallycoded", "com.nexomc")
    }
}

bukkitPluginYaml {
    main = "com.nexomc.nexo.NexoLibs"
    name = "NexoLibs"
    apiVersion = "1.20"
    val version: String by project
    this.version = version
    authors.add("boy0000")
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP

    softDepend = listOf(
        "PlaceholderAPI", "MythicMobs", "MMOItems", "MythicCrucible", "MythicMobs",
        "WorldEdit", "WorldGuard", "Towny", "Factions", "Lands", "PlotSquared",
        "ModelEngine", "HuskTowns", "HuskClaims", "BentoBox", "AxiomPaper"
    )
    libraries = listOf(
        "org.springframework:spring-expression:6.0.6",
        "net.kyori:adventure-text-minimessage:$adventureVersion",
        "net.kyori:adventure-text-serializer-plain:$adventureVersion",
        "net.kyori:adventure-text-serializer-ansi:$adventureVersion",
        "net.kyori:adventure-platform-bukkit:$platformVersion",
        "com.google.code.gson:gson:$googleGsonVersion",
        "org.apache.commons:commons-lang3:$apacheLang3Version",
        "org.apache.httpcomponents.client5:httpclient5:$apacheHttpClientVersion",
    )
}

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
