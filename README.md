# PolyBlur 1.7.10

The Phosphor motion blur from [PolyBlur](https://github.com/Polyfrost/PolyBlur) as a standalone mod for Minecraft 1.7.10. Each frame blends into the next for a trail effect.

Settings: Enabled, Phosphor Mode (Weighted Max, Linear Mix, Alpha Decay), Blur Strength, and Blur Hand.

- Forge: needs [UniMixins](https://github.com/LegacyModdingMC/UniMixins). Open the settings with `/polyblur` or the Config button in the mod list.
- Ornithe: open the settings from Mod Menu, or edit `config/polyblur.properties`.

## Building

`./gradlew build` builds both. The jars land in `forge/build/libs` and `ornithe/build/libs`. The two loaders are separate Gradle builds that share the shaders in `common/`.

## Credits

The blur shader is partly derived from [Sol Client](https://github.com/Sol-Client/Client), and is based on [this StackOverflow answer](https://stackoverflow.com/questions/37913286/glsl-motion-blur-post-processing-2-textures-going-to-the-shader-are-the-same).
