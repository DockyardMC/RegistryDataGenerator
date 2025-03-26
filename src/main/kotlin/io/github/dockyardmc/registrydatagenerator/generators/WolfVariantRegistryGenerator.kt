package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class WolfVariantRegistryGenerator : AbstractDataGenerator<WolfVariant>("wolf_variant", "WolfVariants", "WolfVariantRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.WOLF_VARIANT).get()

        registry.forEach { wolfVariant ->
            val id = registry.getKey(wolfVariant)!!.path
            values[id] = WolfVariant(
                angry = wolfVariant.assetInfo.angry.toDockyard(),
                tame = wolfVariant.assetInfo.tame.toDockyard(),
                wild = wolfVariant.assetInfo.tame.toDockyard(),
                biomes = listOf() // dockyard will not use that anyway
            )
        }
        writeFile { Json.encodeToString<List<WolfVariant>>(values.values.toList()) }
    }
}

fun net.minecraft.core.ClientAsset.toDockyard(): ClientAsset {
    return ClientAsset(this.id.path, this.texturePath.path)
}

@Serializable
data class WolfVariant(
    val angry: ClientAsset,
    val tame: ClientAsset,
    val wild: ClientAsset,
    val biomes: List<String>,
)

@Serializable
data class ClientAsset(
    val id: String,
    val texture: String,
)