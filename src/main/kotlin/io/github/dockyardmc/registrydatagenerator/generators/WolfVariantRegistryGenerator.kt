package io.github.dockyardmc.registrydatagenerator.generators

import cz.lukynka.prettylog.log
import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class WolfVariantRegistryGenerator : AbstractDataGenerator<WolfVariant>("wolf_variant", "WolfVariants", "WolfVariantRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.WOLF_VARIANT).get()

        registry.forEach { wolfVariant ->
            val id = registry.getKey(wolfVariant)!!.toString()
            values[id] = WolfVariant(
                identifier = id,
                angry = wolfVariant.assetInfo.angry.toDockyard().id,
                tame = wolfVariant.assetInfo.tame.toDockyard().id,
                wild = wolfVariant.assetInfo.tame.toDockyard().id,
            )

            log("Wolf - id ${wolfVariant.assetInfo.angry.id} | texture - ${wolfVariant.assetInfo.angry.texturePath.path}")
        }
        writeFile { Json.encodeToString<List<WolfVariant>>(values.values.toList()) }
    }
}

fun net.minecraft.core.ClientAsset.toDockyard(): ClientAsset {
    return ClientAsset(this.id.path, this.texturePath.path)
}

@Serializable
data class WolfVariant(
    val identifier: String,
    val angry: String,
    val tame: String,
    val wild: String,
)

@Serializable
data class ClientAsset(
    val id: String,
    val texture: String,
)