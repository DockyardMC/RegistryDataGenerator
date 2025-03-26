package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class CowVariantDataGenerator : AbstractDataGenerator<CowVariant>("cow_variant", "CowVariants", "CowVariantRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.COW_VARIANT).get()

        registry.forEach { cowVariant ->
            val id = registry.getKey(cowVariant)!!.path
            values[id] = CowVariant(
                model = cowVariant.modelAndTexture.model.serializedName,
                texture = cowVariant.modelAndTexture.asset.toDockyard(),
                biomes = listOf()
            )
        }
        writeFile { Json.encodeToString<List<CowVariant>>(values.values.toList()) }
    }
}

@Serializable
data class CowVariant(
    val model: String,
    val texture: ClientAsset,
    val biomes: List<String>,
)
