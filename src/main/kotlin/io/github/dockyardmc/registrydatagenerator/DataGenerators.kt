package io.github.dockyardmc.registrydatagenerator

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.registrydatagenerator.generators.*

object DataGenerators {

    private val generators: MutableList<DataGenerator> = mutableListOf(
        BlockRegistryGenerator(),
        BiomeRegistryGenerator(),
        EntityRegistryGenerator(),
        ItemRegistryGenerator(),
        SoundListGenerator(),
        BlockListGenerator(),
        EntityTypeListGenerator(),
        ItemListGenerator(),
        ParticleListGenerator(),
    )

    fun run() {
        generators.forEach {
            it.run()
            log("Finished running ${it::class.simpleName}!", LogType.SUCCESS)
        }
    }
}