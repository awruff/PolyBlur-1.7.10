package org.polyfrost.polyblur;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class PolyBlurScreen extends Screen {
    private static final int ENABLED = 0, PHOSPHOR_MODE = 1, STRENGTH = 2, BLUR_HAND = 3, DONE = 4;

    private final Screen parent;

    public PolyBlurScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void init() {
        buttons.clear();
        add(new ButtonWidget(ENABLED, 0, 0, "Enabled: " + onOff(PolyBlurConfig.enabled)));
        add(new ButtonWidget(PHOSPHOR_MODE, 0, 0, "Phosphor Mode: " + PolyBlurConfig.PHOSPHOR_MODES[PolyBlurConfig.phosphorMode]));
        add(new StrengthSlider());
        add(new ButtonWidget(BLUR_HAND, 0, 0, "Blur Hand: " + onOff(PolyBlurConfig.blurHand)));
        add(new ButtonWidget(DONE, 0, 0, "Done"));
    }

    @SuppressWarnings("unchecked")
    private void add(ButtonWidget button) {
        button.x = width / 2 - 100;
        button.y = height / 6 + buttons.size() * 24;
        buttons.add(button);
    }

    private static String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
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
                minecraft.openScreen(parent);
                return;
            default:
                return;
        }
        init();
    }

    @Override
    protected void keyPressed(char chr, int key) {
        if (key == Keyboard.KEY_ESCAPE) minecraft.openScreen(parent);
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        renderBackground();
        drawCenteredString(textRenderer, PolyBlur.NAME, width / 2, height / 6 - 20, 0xFFFFFF);
        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    public void removed() {
        PolyBlurConfig.save();
    }

    private static class StrengthSlider extends ButtonWidget {
        private static final float MIN = 1f, MAX = 10f;

        private boolean dragging;

        StrengthSlider() {
            super(STRENGTH, 0, 0, "");
            updateMessage();
        }

        private void setFromMouse(int mouseX) {
            float t = MathHelper.clamp((float) (mouseX - (x + 4)) / (width - 8), 0f, 1f);
            PolyBlurConfig.strength = Math.round((MIN + t * (MAX - MIN)) * 10) / 10f;
            updateMessage();
        }

        private void updateMessage() {
            message = String.format("Blur Strength: %.1f", PolyBlurConfig.strength);
        }

        @Override
        public int getYImage(boolean hovered) {
            return 0;
        }

        @Override
        protected void renderBackground(Minecraft minecraft, int mouseX, int mouseY) {
            if (!visible) return;
            if (dragging) setFromMouse(mouseX);
            GL11.glColor4f(1f, 1f, 1f, 1f);
            int knob = x + (int) ((PolyBlurConfig.strength - MIN) / (MAX - MIN) * (width - 8));
            drawTexture(knob, y, 0, 66, 4, 20);
            drawTexture(knob + 4, y, 196, 66, 4, 20);
        }

        @Override
        public boolean mouseClicked(Minecraft minecraft, int mouseX, int mouseY) {
            if (!super.mouseClicked(minecraft, mouseX, mouseY)) return false;
            setFromMouse(mouseX);
            dragging = true;
            return true;
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            dragging = false;
        }
    }
}
