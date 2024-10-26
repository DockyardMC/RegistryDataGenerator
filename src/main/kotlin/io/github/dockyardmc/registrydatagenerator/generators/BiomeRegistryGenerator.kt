package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import io.github.dockyardmc.registrydatagenerator.getWorld
import io.github.dockyardmc.registrydatagenerator.mixin.AmbientParticleSettingsAccessor
import io.github.dockyardmc.registrydatagenerator.mixin.BiomeAccessor
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.biome.BiomeSpecialEffects.GrassColorModifier
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream
import kotlin.jvm.optionals.getOrNull

class BiomeRegistryGenerator: DataGenerator {

    val biomes = mutableListOf<Biome>()
    val file = File("./out/biome_registry.json.gz")

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.BIOME).get()
        val particleRegistry = getWorld().registryAccess().lookup(Registries.PARTICLE_TYPE).get()

        registry.forEach { biome ->

            val climateAccessor = (biome as BiomeAccessor).climateSettings
            val downfall = climateAccessor.downfall
            val temperatureModifier = climateAccessor.temperatureModifier

            val particleIsPresent = biome.ambientParticle.isPresent
            val particleKey = if(particleIsPresent) particleRegistry.getKey(biome.ambientParticle.get().options.type) else null
            val particle = if(particleIsPresent) {
                BiomeParticle(ParticleOptions(particleKey!!.path), (biome.ambientParticle.get() as AmbientParticleSettingsAccessor).probability)
            } else null

            val moodSoundIsPresent = biome.ambientMood.isPresent
            val moodSound = if(moodSoundIsPresent) {
                val ambientMood = biome.ambientMood.get()
                MoodSound(ambientMood.blockSearchExtent, ambientMood.soundPositionOffset, sound = ambientMood.soundEvent.registeredName, tickDelay = ambientMood.tickDelay)
            } else null

            val ambientAdditionsPresent = biome.ambientAdditions.isPresent
            val ambientAdditions = if(ambientAdditionsPresent) {
                val ambientAdditions = biome.ambientAdditions.get()
                AdditionsSound(ambientAdditions.soundEvent.registeredName, ambientAdditions.tickChance)
            } else null

            val musicPresent = biome.backgroundMusic.isPresent
            val music = if(musicPresent) {
                val backgroundMusic = biome.backgroundMusic.get()
                BackgroundMusic(backgroundMusic.maxDelay, backgroundMusic.minDelay, backgroundMusic.replaceCurrentMusic(), backgroundMusic.event.registeredName)
            } else null

            val registryBiome = Biome(
                identifier = registry.getKey(biome)!!.toString(),
                downfall = downfall,
                hasRain = biome.hasPrecipitation(),
                temperature = biome.baseTemperature,
                temperatureModifier = if(temperatureModifier == net.minecraft.world.level.biome.Biome.TemperatureModifier.NONE) null else temperatureModifier.name.lowercase(),
                effects = Effects(
                    fogColor = biome.fogColor,
                    grassColor = biome.specialEffects.grassColorOverride.getOrNull(),
                    grassColorModifier = if(biome.specialEffects.grassColorModifier == GrassColorModifier.NONE) null else biome.specialEffects.grassColorModifier.name.lowercase(),
                    foliageColor = if(biome.foliageColor == 0) null else biome.foliageColor,
                    skyColor = biome.skyColor,
                    waterColor = biome.waterColor,
                    waterFogColor = biome.waterFogColor,
                    particle = particle,
                    moodSound = moodSound,
                    ambientAdditions = ambientAdditions,
                    music = music,
                    ambientLoop = if(biome.ambientLoop.isPresent) biome.ambientLoop.get().registeredName else null
                ),
            )
            biomes.add(registryBiome)
        }

        val encodedJson = Json.encodeToString<List<Biome>>(biomes)
        val compressedData = ByteArrayOutputStream()

        val gzipOutputStream = GZIPOutputStream(compressedData)

        gzipOutputStream.write(encodedJson.toByteArray())
        gzipOutputStream.close()

        file.writeBytes(compressedData.toByteArray())
    }
}

@Serializable
data class Biome(
    var identifier: String,
    val downfall: Float,
    var effects: Effects,
    val hasRain: Boolean,
    val temperature: Float,
    val temperatureModifier: String? = null,
)

@Serializable
data class Effects(
    val fogColor: Int? = null,
    val foliageColor: Int? = null,
    val moodSound: MoodSound? = null,
    val music: BackgroundMusic? = null,
    val ambientAdditions: AdditionsSound? = null,
    val ambientLoop: String? = null,
    val particle: BiomeParticle? = null,
    val skyColor: Int,
    val waterColor: Int,
    val waterFogColor: Int,
    val grassColorModifier: String? = null,
    val grassColor: Int? = null
)

@Serializable
data class ParticleOptions(
    val type: String
)

@Serializable
data class BiomeParticle(
    val options: ParticleOptions,
    val probability: Float
)

@Serializable
data class AdditionsSound(
    val sound: String,
    val tickChance: Double
)

@Serializable
data class BackgroundMusic(
    val maxDelay: Int,
    val minDelay: Int,
    val replaceCurrentMusic: Boolean,
    val sound: String
)

@Serializable
data class MoodSound(
    val blockSearchExtent: Int,
    val soundPositionOffset: Double,
    val sound: String,
    val tickDelay: Int
)