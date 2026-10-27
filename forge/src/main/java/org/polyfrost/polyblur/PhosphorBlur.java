package org.polyfrost.polyblur;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.polyfrost.polyblur.mixin.ShaderGroupAccessor;

import java.io.IOException;

public final class PhosphorBlur {
    private static final Logger LOGGER = LogManager.getLogger(PolyBlur.NAME);
    private static final ResourceLocation LOCATION = new ResourceLocation("shaders/post/phosphor_motion_blur.json");
    private static final int MAX_SAMPLER_UNIT = 2;

    private static ShaderGroup group;
    private static int width = -1;
    private static int height = -1;

    public static void renderWorld() {
        if (!PolyBlurConfig.blurHand) render();
    }

    public static void renderFrame() {
        Minecraft mc = Minecraft.getMinecraft();
        if (PolyBlurConfig.blurHand && mc.theWorld != null && mc.getNetHandler() != null) render();
    }

    private static void render() {
        if (!PolyBlurConfig.enabled || !OpenGlHelper.shadersSupported) return;
        Minecraft mc = Minecraft.getMinecraft();
        ShaderGroup shader = getShader(mc.getFramebuffer());
        if (shader == null) return;

        float strength = (float) PolyBlurConfig.strength;
        float feedback = MathHelper.clamp_float(PolyBlurConfig.phosphorMode == 0 ? 0.69f + strength * 0.03f : strength / 10f, 0f, 0.99f);
        for (Shader pass : ((ShaderGroupAccessor) shader).getListShaders()) {
            setUniform(pass, "BlendFactor", feedback);
            setUniform(pass, "Mode", PolyBlurConfig.phosphorMode);
        }

        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        shader.loadShaderGroup(0f);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        for (int unit = MAX_SAMPLER_UNIT; unit >= 1; unit--) {
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit + unit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        mc.getFramebuffer().bindFramebuffer(true);
    }

    private static void setUniform(Shader pass, String name, float value) {
        ShaderUniform uniform = pass.getShaderManager().func_147991_a(name);
        if (uniform != null) uniform.func_148090_a(value);
    }

    private static ShaderGroup getShader(Framebuffer target) {
        if (group != null && target.framebufferWidth == width && target.framebufferHeight == height) return group;
        if (group != null) group.deleteShaderGroup();
        group = null;
        try {
            Minecraft mc = Minecraft.getMinecraft();
            group = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), target, LOCATION);
            group.createBindFramebuffers(target.framebufferWidth, target.framebufferHeight);
            width = target.framebufferWidth;
            height = target.framebufferHeight;
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("Could not load phosphor blur", e);
        }
        return group;
    }
}
