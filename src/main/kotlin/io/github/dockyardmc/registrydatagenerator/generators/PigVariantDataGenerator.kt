package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class PigVariantDataGenerator : AbstractDataGenerator<PigVariant>("pig_variant", "PigVariants", "PigVariantRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.PIG_VARIANT).get()

        registry.forEach { pigVariant ->
            val id = registry.getKey(pigVariant)!!.path
            values[id] = PigVariant(
                model = pigVariant.modelAndTexture.model.serializedName,
                texture = pigVariant.modelAndTexture.asset.toDockyard(),
                biomes = listOf()
            )
        }
        writeFile { Json.encodeToString<List<PigVariant>>(values.values.toList()) }
    }
}

@Serializable
data class PigVariant(
    val model: String,
    val texture: ClientAsset,
    val biomes: List<String>,
)
