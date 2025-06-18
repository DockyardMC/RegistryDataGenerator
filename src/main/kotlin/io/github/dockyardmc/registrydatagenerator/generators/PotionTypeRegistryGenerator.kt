package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.BuiltInRegistries
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream

class PotionTypeRegistryGenerator : DataGenerator {

    private val types: MutableList<PotionType> = mutableListOf()
    private val file = File("./out/potion_type_registry.json.gz")

    override fun run() {
        val registry = BuiltInRegistries.POTION
        registry.forEach { entry ->
            val location = registry.getKey(entry)
            types.add(PotionType(location.toString()))
        }

        val encodedJson = Json.encodeToString<List<PotionType>>(types)
        val compressedData = ByteArrayOutputStream()

        val gzipOutputStream = GZIPOutputStream(compressedData)

        gzipOutputStream.write(encodedJson.toByteArray())
        gzipOutputStream.close()

        file.writeBytes(compressedData.toByteArray())

    }
}

@Serializable
data class PotionType(
    val identifier: String,
)