package io.github.dockyardmc.registrydatagenerator.mixin;

import io.github.dockyardmc.registrydatagenerator.mixins.ServerLoadMixinAdapter;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class ServerLoadMixin {

    @Inject(at = @At("TAIL"), method = "initServer")
    private void initServer(CallbackInfoReturnable<Boolean> cir) {
        ServerLoadMixinAdapter.runServer(cir);
    }

}
