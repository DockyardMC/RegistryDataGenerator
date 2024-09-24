package io.github.dockyardmc.registrydatagenerator

import io.github.dockyardmc.registrydatagenerator.generators.BlockRegistryGenerator

object DataGenerators {

    private val generators: MutableList<DataGenerator> = mutableListOf(BlockRegistryGenerator())

    fun run() {
        generators.forEach {
            it.run()
        }
    }
}