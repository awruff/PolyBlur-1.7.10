import java.util.Properties

plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT"
    id("ploceus") version "1.17.4"
}

val modId = "polyblur"
val minecraftVersion = "1.7.10"
val modVersion = Properties().apply { rootDir.resolve("../gradle.properties").reader().use { load(it) } }
    .getProperty("mod_version")

group = "org.polyfrost"
version = "$modVersion+$minecraftVersion-ornithe"
base.archivesName = modId

ploceus {
    setIntermediaryGeneration(2)
}

repositories {
    maven("https://maven.ornithemc.net/releases") { name = "Ornithe Releases" }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.ornithemc:feather-gen2:$minecraftVersion+build.2:v2")
    modImplementation("net.fabricmc:fabric-loader:0.19.5")
    modCompileOnly("com.terraformersmc:modmenu:0.5.0+mc$minecraftVersion")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
        vendor = JvmVendorSpec.ADOPTIUM
    }
    withSourcesJar()
}

loom {
    runConfigs.all {
        runDirectory = file("run")
        programArgs("--userProperties", "{}")
    }
    runConfigs.remove(runConfigs["server"])
}

sourceSets.main {
    resources.srcDir("../common")
}

tasks.processResources {
    inputs.property("version", modVersion)
    filesMatching("fabric.mod.json") { expand("version" to modVersion) }
}

tasks.jar {
    from("../LICENSE") { rename { "${it}_$modId" } }
}
