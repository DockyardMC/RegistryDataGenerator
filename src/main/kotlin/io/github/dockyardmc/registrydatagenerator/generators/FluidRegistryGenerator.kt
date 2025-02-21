package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.BuiltInRegistries
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream
import kotlin.jvm.optionals.getOrNull

class FluidRegistryGenerator: DataGenerator {

    private val fluids: MutableList<Fluid> = mutableListOf()
    private val file = File("./out/fluid_registry.json.gz")

    override fun run() {
        BuiltInRegistries.FLUID.forEach { fluid ->
            val identifier = BuiltInRegistries.FLUID.getKey(fluid).toString()
            val default = fluid.defaultFluidState()
            val dripParticle = default.dripParticle?.type?.let { BuiltInRegistries.PARTICLE_TYPE.getKey(it)?.toString() }
            val pickupSound = fluid.pickupSound.getOrNull()?.location.toString()
            val explosionResistance = default.explosionResistance
            val block = BuiltInRegistries.BLOCK.getKey(default.createLegacyBlock().block).toString()
            val outputFluid = Fluid(identifier, dripParticle, pickupSound, explosionResistance, block)
            fluids.add(outputFluid)
        }

        val encodedJson = Json.encodeToString<List<Fluid>>(fluids)
        val compressedData = ByteArrayOutputStream()

        val gzipOutputStream = GZIPOutputStream(compressedData)

        gzipOutputStream.write(encodedJson.toByteArray())
        gzipOutputStream.close()

        file.writeBytes(compressedData.toByteArray())
    }
}

@Serializable
data class Fluid(
    val identifier: String,
    val dripParticle: String?,
    val pickupSound: String,
    val explosionResistance: Float,
    val block: String
)