package io.github.dockyardmc.registrydatagenerator.mixins

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.registrydatagenerator.DataGenerators
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object ServerLoadMixinAdapter {

    @JvmStatic
    fun runServer(callbackInfo: CallbackInfo) {
        log("Starting data generator..", LogType.RUNTIME)

        DataGenerators.run()

        log("Data generation done!", LogType.SUCCESS)
        Runtime.getRuntime().halt(0)
    }

}