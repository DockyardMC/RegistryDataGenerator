package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream

class AttributeRegistryGenerator: DataGenerator {

    val attributes = mutableListOf<Attribute>()
    private val file = File("./out/attribute_registry.json.gz")

    override fun run() {
        val registry = BuiltInRegistries.ATTRIBUTE
        registry.forEach { attribute ->
            val identifier = registry.getKey(attribute)
            val default = attribute.defaultValue
            val translationKey = attribute.descriptionId
            val clientSync = attribute.isClientSyncable

            var minValue: Double? = null
            var maxValue: Double? = null

            if(attribute is RangedAttribute) {
                minValue = attribute.minValue
                maxValue = attribute.maxValue
            }

            attributes.add(Attribute(identifier.toString(), translationKey, default, clientSync, minValue, maxValue))
        }

        val encodedJson = Json.encodeToString<List<Attribute>>(attributes)
        val compressedData = ByteArrayOutputStream()

        val gzipOutputStream = GZIPOutputStream(compressedData)

        gzipOutputStream.write(encodedJson.toByteArray())
        gzipOutputStream.close()

        file.writeBytes(compressedData.toByteArray())
    }
}

@Serializable
data class Attribute(
    val identifier: String,
    val translationKey: String,
    val defaultValue: Double,
    val clientSync: Boolean,
    val minValue: Double? = null,
    val maxValue: Double? = null
)