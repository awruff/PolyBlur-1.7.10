package org.polyfrost.polyblur;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Properties;

public final class PolyBlurConfig {
    public static final String[] PHOSPHOR_MODES = {"Weighted Max", "Linear Mix", "Alpha Decay"};

    public static boolean enabled = true;
    public static int phosphorMode = 2;
    public static float strength = 3f;
    public static boolean blurHand = true;

    private static final File FILE = FabricLoader.getInstance().getConfigDir().resolve("polyblur.properties").toFile();

    private PolyBlurConfig() {
    }

    static void load() {
        if (!FILE.exists()) return;
        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(FILE.toPath())) {
            props.load(reader);
            enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
            phosphorMode = MathHelper.clamp(Integer.parseInt(props.getProperty("phosphorMode", "2")), 0, 2);
            strength = MathHelper.clamp(Float.parseFloat(props.getProperty("strength", "3")), 1f, 10f);
            blurHand = Boolean.parseBoolean(props.getProperty("blurHand", "true"));
        } catch (IOException | NumberFormatException e) {
            LogManager.getLogger(PolyBlur.NAME).error("Could not read {}", FILE, e);
        }
    }

    static void save() {
        Properties props = new Properties();
        props.setProperty("enabled", String.valueOf(enabled));
        props.setProperty("phosphorMode", String.valueOf(phosphorMode));
        props.setProperty("strength", String.valueOf(strength));
        props.setProperty("blurHand", String.valueOf(blurHand));
        try (Writer writer = Files.newBufferedWriter(FILE.toPath())) {
            props.store(writer, null);
        } catch (IOException e) {
            LogManager.getLogger(PolyBlur.NAME).error("Could not write {}", FILE, e);
        }
    }
}
