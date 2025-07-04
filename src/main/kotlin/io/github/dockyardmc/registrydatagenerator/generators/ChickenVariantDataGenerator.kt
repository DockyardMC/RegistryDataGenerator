package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class ChickenVariantDataGenerator : AbstractDataGenerator<ChickenVariant>("chicken_variant", "ChickenVariants", "ChickenVariantRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.CHICKEN_VARIANT).get()

        registry.forEach { chickenVariant ->
            val id = registry.getKey(chickenVariant)!!.toString()
            values[id] = ChickenVariant(
                identifier = id,
                assetId = chickenVariant.modelAndTexture.asset.toDockyard().id,
            )
        }
        writeFile { Json.encodeToString<List<ChickenVariant>>(values.values.toList()) }
    }
}

@Serializable
data class ChickenVariant(
    val identifier: String,
    val assetId: String,
)
