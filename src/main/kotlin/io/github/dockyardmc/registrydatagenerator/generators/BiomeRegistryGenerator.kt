package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import io.github.dockyardmc.registrydatagenerator.getWorld
import io.github.dockyardmc.registrydatagenerator.mixin.AmbientParticleSettingsAccessor
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPOutputStream

class BiomeRegistryGenerator: DataGenerator {

    val biomes = mutableListOf<Biome>()
    val file = File("./out/biome_registry.json.gz")

    override fun run() {
        val registry = getWorld().registryAccess().registry(Registries.BIOME).get()
        val particleRegistry = getWorld().registryAccess().registry(Registries.PARTICLE_TYPE).get()

        val protocolIdCounter = AtomicInteger()

        registry.forEach { biome ->

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
                downfall = biome.getPrecipitationAt(BlockPos.ZERO).name.lowercase(),
                hasRain = biome.hasPrecipitation(),
                temperature = biome.baseTemperature,
                effects = Effects(
                    fogColor = biome.fogColor,
                    foliageColor = biome.foliageColor,
                    skyColor = biome.skyColor,
                    waterColor = biome.waterColor,
                    waterFogColor = biome.waterFogColor,
                    particle = particle,
                    moodSound = moodSound,
                    ambientAdditions = ambientAdditions,
                    music = music,
                    ambientLoop = if(biome.ambientLoop.isPresent) biome.ambientLoop.get().registeredName else null
                ),
                protocolId =protocolIdCounter.getAndIncrement()
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
    val downfall: String = "rain",
    var effects: Effects,
    val hasRain: Boolean = false,
    val temperature: Float = 1f,
    val protocolId: Int,
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
    val waterFogColor: Int
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