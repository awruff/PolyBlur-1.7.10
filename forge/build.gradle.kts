import java.util.Properties

plugins {
    id("java")
    id("com.gtnewhorizons.retrofuturagradle") version "2.0.6"
}

val modId = "polyblur"
val minecraftVersion = "1.7.10"
val unimixins = "io.github.legacymoddingmc:unimixins:0.3.1:dev"
val modVersion = Properties().apply { rootDir.resolve("../gradle.properties").reader().use { load(it) } }
    .getProperty("mod_version")

group = "org.polyfrost"
version = "$modVersion+$minecraftVersion-forge"
base.archivesName = modId

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
        vendor = JvmVendorSpec.AZUL
    }
    withSourcesJar()
}

minecraft {
    mcVersion = minecraftVersion
    username = "Developer"
    extraRunJvmArguments.add("-Dmixin.debug.export=true")
    extraTweakClasses.add("org.spongepowered.asm.launch.MixinTweaker")
    injectedTags.put("VERSION", modVersion)
}

tasks.injectTags {
    outputClassName = "org.polyfrost.polyblur.Tags"
}

repositories {
    maven("https://nexus.gtnewhorizons.com/repository/public/") { name = "GTNH Maven" }
}

dependencies {
    annotationProcessor("org.ow2.asm:asm-debug-all:5.0.4")
    annotationProcessor("com.google.guava:guava:24.1.1-jre")
    annotationProcessor("com.google.code.gson:gson:2.13.2")
    annotationProcessor(unimixins)
    implementation(modUtils.enableMixins(unimixins, "mixins.$modId.refmap.json"))
}

sourceSets.main {
    resources.srcDir("../common")
}

tasks.processResources {
    val props = mapOf("version" to modVersion, "mcversion" to minecraftVersion)
    inputs.properties(props)
    filesMatching("mcmod.info") { expand(props) }
}

tasks.jar {
    manifest.attributes(
        "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
        "MixinConfigs" to "mixins.$modId.json",
        "ForceLoadAsMod" to true,
        "FMLCorePluginContainsFMLMod" to true,
    )
    from("../LICENSE") { rename { "${it}_$modId" } }
}
