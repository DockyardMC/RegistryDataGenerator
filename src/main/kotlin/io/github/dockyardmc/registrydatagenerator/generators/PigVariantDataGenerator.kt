package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class PigVariantDataGenerator : AbstractDataGenerator<PigVariant>("pig_variant", "PigVariants", "PigVariantRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.PIG_VARIANT).get()

        registry.forEach { pigVariant ->
            val id = registry.getKey(pigVariant)!!.toString()
            values[id] = PigVariant(
                identifier = id,
                assetId = pigVariant.modelAndTexture.asset.toDockyard().id,
            )
        }
        writeFile { Json.encodeToString<List<PigVariant>>(values.values.toList()) }
    }
}

@Serializable
data class PigVariant(
    val identifier: String,
    val assetId: String,
)
