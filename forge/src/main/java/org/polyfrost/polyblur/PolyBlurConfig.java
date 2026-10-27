package org.polyfrost.polyblur;

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class PolyBlurConfig {
    public static final String[] PHOSPHOR_MODES = {"Weighted Max", "Linear Mix", "Alpha Decay"};

    public static boolean enabled = true;
    public static int phosphorMode = 2;
    public static double strength = 3;
    public static boolean blurHand = true;

    private static Configuration config;

    private PolyBlurConfig() {
    }

    static void load(File file) {
        config = new Configuration(file);
        String c = Configuration.CATEGORY_GENERAL;
        enabled = config.get(c, "enabled", true, "Turns PolyBlur on or off.").getBoolean();
        phosphorMode = MathHelper.clamp_int(config.get(c, "phosphorMode", 2, "0 = Weighted Max, 1 = Linear Mix, 2 = Alpha Decay", 0, 2).getInt(), 0, 2);
        strength = MathHelper.clamp_double(config.get(c, "strength", 3.0, "How strong the blur is.", 1.0, 10.0).getDouble(), 1.0, 10.0);
        blurHand = config.get(c, "blurHand", true, "Blurs your held item along with the world.").getBoolean();
        if (config.hasChanged()) config.save();
    }

    static void save() {
        String c = Configuration.CATEGORY_GENERAL;
        config.get(c, "enabled", true).set(enabled);
        config.get(c, "phosphorMode", 2).set(phosphorMode);
        config.get(c, "strength", 3.0).set(strength);
        config.get(c, "blurHand", true).set(blurHand);
        config.save();
    }
}
