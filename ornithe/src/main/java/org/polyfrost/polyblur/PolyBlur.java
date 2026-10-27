package org.polyfrost.polyblur;

import net.fabricmc.api.ClientModInitializer;

public class PolyBlur implements ClientModInitializer {
    public static final String NAME = "PolyBlur";

    @Override
    public void onInitializeClient() {
        PolyBlurConfig.load();
    }
}
