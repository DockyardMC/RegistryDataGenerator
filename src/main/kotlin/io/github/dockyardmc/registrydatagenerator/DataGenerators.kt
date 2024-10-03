package io.github.dockyardmc.registrydatagenerator

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.registrydatagenerator.generators.BiomeRegistryGenerator
import io.github.dockyardmc.registrydatagenerator.generators.BlockRegistryGenerator
import io.github.dockyardmc.registrydatagenerator.generators.EntityRegistryGenerator
import io.github.dockyardmc.registrydatagenerator.generators.SoundRegistryGenerator

object DataGenerators {

    private val generators: MutableList<DataGenerator> = mutableListOf(BlockRegistryGenerator(), BiomeRegistryGenerator(), EntityRegistryGenerator(), SoundRegistryGenerator())

    fun run() {
        generators.forEach {
            it.run()
            log("Finished running ${it::class.simpleName}!", LogType.SUCCESS)
        }
    }
}