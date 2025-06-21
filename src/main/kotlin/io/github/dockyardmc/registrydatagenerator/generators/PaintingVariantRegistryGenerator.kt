package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class PaintingVariantRegistryGenerator : AbstractDataGenerator<PaintingVariant>("painting_variant_registry", "PaintingVariants", "PaintingVariantRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.PAINTING_VARIANT).get()
        registry.forEach { entry ->
            val identifier = registry.getKey(entry).toString()
            val assetId = entry.assetId.toString()
            val height = entry.height
            val width = entry.width
            values[identifier] = PaintingVariant(identifier, assetId, height, width)
        }
        this.writeFile { Json.encodeToString(values.values.toList()) }
    }
}

@Serializable
data class PaintingVariant(
    val identifier: String,
    val assetId: String,
    val height: Int,
    val width: Int,
)