package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.BuiltInRegistries
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream

class ParticleRegistryGenerator: DataGenerator {

    private val particles: MutableList<Particle> = mutableListOf()
    private val file = File("./out/particle_registry.json.gz")

    override fun run() {
        val registry = BuiltInRegistries.PARTICLE_TYPE
        registry.forEach { entry ->
            val identifier = registry.getKey(entry).toString()
            val overrideLimiter = entry.overrideLimiter
            val particle = Particle(identifier, overrideLimiter)

            particles.add(particle)
        }

        val encodedJson = Json.encodeToString<List<Particle>>(particles)
        val compressedData = ByteArrayOutputStream()

        val gzipOutputStream = GZIPOutputStream(compressedData)

        gzipOutputStream.write(encodedJson.toByteArray())
        gzipOutputStream.close()

        file.writeBytes(compressedData.toByteArray())
    }
}

@Serializable
data class Particle(
    val identifier: String,
    val overrideLimiter: Boolean,
)