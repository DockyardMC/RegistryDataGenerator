package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component

class TrimPatternRegistryGenerator : AbstractDataGenerator<TrimPattern>("trim_pattern_registry", "TrimPatterns", "TrimPatternRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.TRIM_PATTERN).get()
        registry.forEach { entry ->
            val identifier = registry.getKey(entry).toString()
            val assetId = entry.assetId.toString()
            val decal = entry.decal
            val description = entry.description
            values[identifier] = TrimPattern(identifier, assetId, decal, description)
        }
        this.writeFile { Json.encodeToString(values.values.toList()) }
    }
}


@Serializable
data class TrimPattern(
    val identifier: String,
    val assetId: String,
    val decal: Boolean,
    @Serializable(with = ComponentToJsonSerializer::class)
    val description: Component,
)