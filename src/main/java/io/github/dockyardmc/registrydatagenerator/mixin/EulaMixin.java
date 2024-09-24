package io.github.dockyardmc.registrydatagenerator.mixin;

import net.minecraft.server.Eula;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Eula.class)
public class EulaMixin {

    @Inject(at = @At("TAIL"), method = "hasAgreedToEULA", cancellable = true)
    private void hasAgreedToEula(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

}
