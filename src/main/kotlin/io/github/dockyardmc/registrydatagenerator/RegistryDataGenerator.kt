package io.github.dockyardmc.registrydatagenerator

import cz.lukynka.prettylog.LoggerSettings
import cz.lukynka.prettylog.LoggerStyle
import net.fabricmc.api.ModInitializer

class RegistryDataGenerator : ModInitializer {

    override fun onInitialize() {
        LoggerSettings.saveToFile = false
        LoggerSettings.loggerStyle = LoggerStyle.BRACKET_PREFIX_WHITE_TEXT

        FileManager.delete()
        FileManager.create()
    }
}
