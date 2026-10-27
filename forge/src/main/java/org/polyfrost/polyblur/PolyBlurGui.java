package org.polyfrost.polyblur;

import cpw.mods.fml.client.config.GuiSlider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class PolyBlurGui extends GuiScreen {
    private static final int ENABLED = 0, PHOSPHOR_MODE = 1, STRENGTH = 2, BLUR_HAND = 3, DONE = 4;

    private final GuiScreen parent;

    public PolyBlurGui(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        add(new GuiButton(ENABLED, 0, 0, "Enabled: " + onOff(PolyBlurConfig.enabled)));
        add(new GuiButton(PHOSPHOR_MODE, 0, 0, "Phosphor Mode: " + PolyBlurConfig.PHOSPHOR_MODES[PolyBlurConfig.phosphorMode]));
        add(new GuiSlider(STRENGTH, 0, 0, 200, 20, "Blur Strength: ", "", 1, 10, PolyBlurConfig.strength, true, true,
                slider -> PolyBlurConfig.strength = Math.round(slider.getValue() * 10) / 10.0));
        add(new GuiButton(BLUR_HAND, 0, 0, "Blur Hand: " + onOff(PolyBlurConfig.blurHand)));
        add(new GuiButton(DONE, 0, 0, "Done"));
    }

    @SuppressWarnings("unchecked")
    private void add(GuiButton button) {
        button.xPosition = width / 2 - 100;
        button.yPosition = height / 6 + buttonList.size() * 24;
        buttonList.add(button);
    }

    private static String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case ENABLED:
                PolyBlurConfig.enabled = !PolyBlurConfig.enabled;
                break;
            case PHOSPHOR_MODE:
                PolyBlurConfig.phosphorMode = (PolyBlurConfig.phosphorMode + 1) % PolyBlurConfig.PHOSPHOR_MODES.length;
                break;
            case BLUR_HAND:
                PolyBlurConfig.blurHand = !PolyBlurConfig.blurHand;
                break;
            case DONE:
                mc.displayGuiScreen(parent);
                return;
            default:
                return;
        }
        initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) mc.displayGuiScreen(parent);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, PolyBlur.NAME, width / 2, height / 6 - 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        PolyBlurConfig.save();
    }
}
