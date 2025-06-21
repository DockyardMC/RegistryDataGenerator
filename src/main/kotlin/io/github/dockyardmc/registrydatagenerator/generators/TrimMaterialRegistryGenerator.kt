package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component

class TrimMaterialRegistryGenerator : AbstractDataGenerator<TrimMaterial>("trim_material_registry", "TrimMaterials", "TrimMaterialRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.TRIM_MATERIAL).get()
        registry.forEach { entry ->
            val identifier = registry.getKey(entry).toString()
            val assetName = entry.assets.base.toString()
            val description = entry.description
            val overrides = entry.assets.overrides.mapKeys { map -> map.key.toString() }.mapValues { map -> map.value.toString() }
            values[identifier] = TrimMaterial(identifier, assetName, description, overrides)
        }
        this.writeFile { Json.encodeToString(values.values.toList()) }
    }
}

@Serializable
data class TrimMaterial(
    val identifier: String,
    val assetName: String,
    @Serializable(with = ComponentToJsonSerializer::class)
    val description: Component,
    val overrideArmorMaterials: Map<String, String>? = null,
)