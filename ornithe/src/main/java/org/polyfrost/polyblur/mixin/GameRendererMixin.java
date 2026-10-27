package org.polyfrost.polyblur.mixin;

import net.minecraft.client.render.GameRenderer;
import org.polyfrost.polyblur.PhosphorBlur;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderWorld(FJ)V", shift = At.Shift.AFTER))
    private void polyblur$renderFrame(CallbackInfo ci) {
        PhosphorBlur.renderFrame();
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=hand", shift = At.Shift.AFTER))
    private void polyblur$renderWorld(CallbackInfo ci) {
        PhosphorBlur.renderWorld();
    }
}
