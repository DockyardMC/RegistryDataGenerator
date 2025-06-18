package io.github.dockyardmc.registrydatagenerator.generators

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.registrydatagenerator.DataGenerator
import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream

class TagRegistryGenerator : DataGenerator {

    companion object {
        val registries: Map<Registry<*>, String> = mapOf(
            getWorld().registryAccess().lookup(Registries.BIOME).get() to "Biome",
            BuiltInRegistries.BLOCK to "Block",
            BuiltInRegistries.ENTITY_TYPE to "EntityType",
            BuiltInRegistries.FLUID to "Fluid",
            BuiltInRegistries.ITEM to "Item"
        )

        fun getTags(registry: Registry<*>): List<Tag> {
            val outputTags = mutableListOf<Tag>()
            registry.tags.filter { it.key().location.namespace == "minecraft" }.forEach { tag ->
                val identifier = tag.key().location.toString()
                val tags = tag.toList().map { it.registeredName }
                val outputTag = Tag(identifier, tags.toSet(), registry.key().location().toString())
                log("$outputTag")
                outputTags.add(outputTag)
            }

            return outputTags
        }
    }

    override fun run() {
        registries.keys.forEach(::generate)
    }

    private fun generate(registry: Registry<*>) {
        val name = registry.key().location().toString().replace("minecraft:", "")
        val tags = getTags(registry)
        saveToFile(tags, name)
        log("Generated tags for $name!", LogType.SUCCESS)
    }

    private fun saveToFile(tags: List<Tag>, name: String) {
        val fileName = if(name.contains("/")) "./out/${name.split("/")[1]}_tags.json.gz" else "./out/${name}_tags.json.gz"
        val file = File(fileName)

        val encodedJson = Json.encodeToString<List<Tag>>(tags)
        val compressedData = ByteArrayOutputStream()

        val gzipOutputStream = GZIPOutputStream(compressedData)

        gzipOutputStream.write(encodedJson.toByteArray())
        gzipOutputStream.close()

        file.writeBytes(compressedData.toByteArray())
    }
}

@Serializable
data class Tag(
    val identifier: String,
    val tags: Set<String>,
    val registryIdentifier: String,
)