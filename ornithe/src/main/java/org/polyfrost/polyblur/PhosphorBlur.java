package org.polyfrost.polyblur;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.PostChain;
import net.minecraft.client.render.PostPass;
import net.minecraft.client.render.pipeline.RenderTarget;
import net.minecraft.client.render.platform.GLX;
import net.minecraft.client.render.shaders.Uniform;
import net.minecraft.client.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.polyfrost.polyblur.mixin.PostChainAccessor;

public final class PhosphorBlur {
    private static final Logger LOGGER = LogManager.getLogger(PolyBlur.NAME);
    private static final Identifier LOCATION = new Identifier("shaders/post/phosphor_motion_blur.json");
    private static final int MAX_SAMPLER_UNIT = 2;

    private static PostChain chain;
    private static int width = -1;
    private static int height = -1;

    private PhosphorBlur() {
    }

    public static void renderWorld() {
        if (!PolyBlurConfig.blurHand) render();
    }

    public static void renderFrame() {
        Minecraft mc = Minecraft.getInstance();
        if (PolyBlurConfig.blurHand && mc.world != null && mc.getNetworkHandler() != null) render();
    }

    private static void render() {
        if (!PolyBlurConfig.enabled || !GLX.usePostProcess) return;
        Minecraft mc = Minecraft.getInstance();
        PostChain shader = getShader(mc.getRenderTarget());
        if (shader == null) return;

        float strength = PolyBlurConfig.strength;
        float feedback = MathHelper.clamp(PolyBlurConfig.phosphorMode == 0 ? 0.69f + strength * 0.03f : strength / 10f, 0f, 0.99f);
        for (PostPass pass : ((PostChainAccessor) shader).getPasses()) {
            setUniform(pass, "BlendFactor", feedback);
            setUniform(pass, "Mode", PolyBlurConfig.phosphorMode);
        }

        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        shader.process(0f);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        for (int unit = MAX_SAMPLER_UNIT; unit >= 1; unit--) {
            GLX.activeTexture(GLX.GL_TEXTURE0 + unit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
        GLX.activeTexture(GLX.GL_TEXTURE0);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        mc.getRenderTarget().bindWrite(true);
    }

    private static void setUniform(PostPass pass, String name, float value) {
        Uniform uniform = pass.getEffect().getUniform(name);
        if (uniform != null) uniform.set(value);
    }

    private static PostChain getShader(RenderTarget target) {
        if (chain != null && target.viewWidth == width && target.viewHeight == height) return chain;
        if (chain != null) chain.close();
        chain = null;
        try {
            Minecraft mc = Minecraft.getInstance();
            chain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), target, LOCATION);
            chain.resize(target.viewWidth, target.viewHeight);
            width = target.viewWidth;
            height = target.viewHeight;
        } catch (Exception e) {
            LOGGER.error("Could not load phosphor blur", e);
        }
        return chain;
    }
}
