package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class CowVariantDataGenerator : AbstractDataGenerator<CowVariant>("cow_variant", "CowVariants", "CowVariantRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.COW_VARIANT).get()

        registry.forEach { cowVariant ->
            val id = registry.getKey(cowVariant)!!.toString()
            values[id] = CowVariant(
                identifier = id,
                assetId = cowVariant.modelAndTexture.asset.toDockyard().id,
            )
        }
        writeFile { Json.encodeToString<List<CowVariant>>(values.values.toList()) }
    }
}

@Serializable
data class CowVariant(
    val identifier: String,
    val assetId: String,
)
