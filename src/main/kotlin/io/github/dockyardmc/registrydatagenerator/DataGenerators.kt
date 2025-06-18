package io.github.dockyardmc.registrydatagenerator

import com.google.common.hash.HashCode
import com.mojang.serialization.codecs.RecordCodecBuilder
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.registrydatagenerator.generators.*
import io.github.dockyardmc.registrydatagenerator.generators.lists.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.util.HashOps
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
        PotionTypeRegistryGenerator()
    )

    class HashTest(val tag: CompoundTag) {
        companion object {
            val CODEC = RecordCodecBuilder.create { instance ->
                instance.group(
                    CompoundTag.CODEC.fieldOf("tag").forGetter(HashTest::tag)
                ).apply(instance, ::HashTest)
            }
        }
    }

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