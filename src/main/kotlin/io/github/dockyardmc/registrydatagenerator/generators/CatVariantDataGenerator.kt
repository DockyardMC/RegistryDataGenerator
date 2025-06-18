package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class CatVariantDataGenerator : AbstractDataGenerator<CatVariant>("cat_variant", "CatVariants", "CatVariantRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.CAT_VARIANT).get()

        registry.forEach { catVariant ->
            val id = registry.getKey(catVariant)!!.toString()
            values[id] = CatVariant(
                identifier = id,
                assetId = catVariant.assetInfo.toDockyard().id,
            )
        }
        writeFile { Json.encodeToString<List<CatVariant>>(values.values.toList()) }
    }
}

@Serializable
data class CatVariant(
    val identifier: String,
    val assetId: String,
)
