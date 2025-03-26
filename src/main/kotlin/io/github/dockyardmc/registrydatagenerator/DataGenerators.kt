package io.github.dockyardmc.registrydatagenerator

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.registrydatagenerator.generators.*
import io.github.dockyardmc.registrydatagenerator.generators.lists.*
import java.io.File

object DataGenerators {

    val generators: MutableList<DataGenerator> = mutableListOf(
        BlockRegistryGenerator(),
        BiomeRegistryGenerator(),
        EntityRegistryGenerator(),
        ItemRegistryGenerator(),
        SoundListGenerator(),
        BlockListGenerator(),
        EntityTypeListGenerator(),
        ItemListGenerator(),
        ParticleRegistryGenerator(),
        ParticleListGenerator(),
        TagRegistryGenerator(),
        TagListGenerator(),
        FluidRegistryGenerator(),
        AttributeRegistryGenerator(),
        AttributeListGenerator(),
        WolfVariantRegistryGenerator(),
        WolfSoundVariantRegistryGenerator(),
        ChickenVariantDataGenerator(),
        PigVariantDataGenerator(),
        CowVariantDataGenerator(),
        CatVariantDataGenerator(),
    )

    fun run() {

        File("./out/classes/").mkdirs()

        generators.forEach {
            it.run()
            log("Finished running ${it::class.simpleName}!", LogType.SUCCESS)
        }
    }
}