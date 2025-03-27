package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class FrogVariantDataGenerator : AbstractDataGenerator<FrogVariant>("frog_variant", "FrogVariants", "FrogVariantRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.FROG_VARIANT).get()

        registry.forEach { frogVariant ->
            val id = registry.getKey(frogVariant)!!.path
            values[id] = FrogVariant(
                identifier = id,
                assetId = frogVariant.assetInfo.toDockyard().id,
            )
        }
        writeFile { Json.encodeToString<List<FrogVariant>>(values.values.toList()) }
    }
}

@Serializable
data class FrogVariant(
    val identifier: String,
    val assetId: String,
)
