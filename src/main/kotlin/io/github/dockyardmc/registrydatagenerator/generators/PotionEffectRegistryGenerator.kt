package io.github.dockyardmc.registrydatagenerator.generators

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.BuiltInRegistries

class PotionEffectRegistryGenerator : AbstractDataGenerator<PotionEffect>("potion_effect_registry", "PotionEffects", "PotionEffectRegistry") {

    override fun run() {
        val registry = BuiltInRegistries.MOB_EFFECT
        registry.forEach { entry ->
            val identifier = registry.getKey(entry).toString()
            val name = entry.displayName.string
            val type = PotionEffect.Type.entries[entry.category.ordinal]
            val isInstant = entry.isInstantenous
            val color = entry.color
            values[identifier] = PotionEffect(identifier, name, type, isInstant, color)
        }
        this.writeFile { Json.encodeToString(values.values.toList()) }
    }
}

@Serializable
data class PotionEffect(
    val identifier: String,
    val name: String,
    val type: Type,
    val isInstant: Boolean,
    val color: Int,

    ) {
    enum class Type {
        BENEFICIAL,
        HARMFUL,
        NEUTRAL
    }
}