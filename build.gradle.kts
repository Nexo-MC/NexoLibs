import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.Permission
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries

plugins {
    id("java")
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.papermc")
    //id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.autoversion")
    id("xyz.jpenilla.run-paper") version "2.3.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
    id("io.papermc.paperweight.userdev") version "1.7.5" apply false
    id("com.gradleup.shadow") version "8.3.5"
}

val compiled = (project.findProperty("nexo_compiled")?.toString() ?: "true").toBoolean()
val pluginPath = project.findProperty("nexo_plugin_path")?.toString()
val pluginVersion: String by project

val commandApiVersion = "9.6.1"
val adventureVersion = "4.17.0"
val platformVersion = "4.3.4"
val googleGsonVersion = "2.11.0"
val apacheLang3Version = "3.17.0"
val apacheHttpClientVersion = "5.4"
val creativeVersion = "1.7.3"

version = pluginVersion

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
    implementation(idofrontLibs.kotlin.stdlib)

    implementation("net.kyori:adventure-text-minimessage:$adventureVersion")
    implementation("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    implementation("net.kyori:adventure-text-serializer-ansi:$adventureVersion")
    implementation("net.kyori:adventure-platform-bukkit:$platformVersion")

    implementation("team.unnamed:creative-api:$creativeVersion") { exclude("net.kyori") }
    implementation("team.unnamed:creative-server:$creativeVersion")
    implementation("team.unnamed:creative-serializer-minecraft:1.7.4-SNAPSHOT") { exclude("net.kyori") }

    implementation("dev.jorel:commandapi-bukkit-shade:$commandApiVersion")
    implementation("dev.jorel:commandapi-bukkit-kotlin:$commandApiVersion")

    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("org.glassfish:javax.json:1.1.4")
    implementation("io.th0rgal:protectionlib:1.6.2")
    implementation("org.springframework:spring-expression:6.0.8")
    implementation("org.apache.commons:commons-lang3:$apacheLang3Version")
    implementation("org.apache.httpcomponents.client5:httpclient5:$apacheHttpClientVersion")
    implementation("com.google.code.gson:gson:$googleGsonVersion")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.12")
    implementation("com.jeff-media:custom-block-data:2.2.2")
    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")
    implementation("com.jeff-media:persistent-data-serializer:1.0")
    implementation("org.jetbrains:annotations:26.0.1") { isTransitive = false }
    implementation("dev.triumphteam:triumph-gui:3.1.10") { exclude("net.kyori") }
    implementation("gs.mclo:java:2.2.1")

    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.spongepowered:configurate-extra-kotlin:4.1.2")

    implementation("me.gabytm.util:actions-spigot:1.0.0-SNAPSHOT") { exclude(group = "com.google.guava") }
}

if (pluginPath != null) {
    tasks {
        val defaultPath = findByName("reobfJar") ?: findByName("shadowJar") ?: findByName("jar")
        // Define the main copy task
        val copyJarTask = register<Copy>("copyJar") {
            this.doNotTrackState("Overwrites the plugin jar to allow for easier reloading")
            dependsOn(shadowJar, jar)
            from(defaultPath)
            into(pluginPath)
            doLast {
                println("Copied to plugin directory $pluginPath")
                Path(pluginPath).listDirectoryEntries()
                    .filter { it.fileName.toString().matches("nexo-.*.jar".toRegex()) }
                    .filterNot { it.fileName.toString().endsWith("$pluginVersion.jar") }
                    .forEach { delete(it) }
            }
        }

        // Make the build task depend on all individual copy tasks
        named<DefaultTask>("build").get().dependsOn(copyJarTask)
    }
}

bukkitPluginYaml {
    main = "com.codecraft.nexo.NexoLibs"
    name = "NexoLibs"
    apiVersion = "1.20"
    version = pluginVersion
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
