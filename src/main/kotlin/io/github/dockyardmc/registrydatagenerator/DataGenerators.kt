package io.github.dockyardmc.registrydatagenerator

import com.mojang.serialization.codecs.RecordCodecBuilder
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.registrydatagenerator.generators.*
import io.github.dockyardmc.registrydatagenerator.generators.lists.*
import net.minecraft.nbt.CompoundTag
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
        FrogVariantDataGenerator(),
        PotionTypeRegistryGenerator(),
        BannerPatternRegistryDataGenerator(),
        DamageTypeRegistryGenerator(),
        JukeboxSongRegistryGenerator(),
        TrimMaterialRegistryGenerator(),
        TrimPatternRegistryGenerator(),
        PaintingVariantRegistryGenerator(),
        PotionEffectRegistryGenerator()
    )

    fun run() {

        File("./out/classes/").mkdirs()
        val tag = CompoundTag()
        tag.putInt("test", 5)

        generators.forEach {
            it.run()
            log("Finished running ${it::class.simpleName}!", LogType.SUCCESS)
        }
    }
}