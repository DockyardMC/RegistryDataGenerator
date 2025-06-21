package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class DamageTypeRegistryGenerator : AbstractDataGenerator<DamageType>("damage_type_registry", "DamageTypes", "DamageTypeRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.DAMAGE_TYPE).get()
        registry.forEach { entry ->
            val identifier = registry.getKey(entry).toString()
            val exhaustion = entry.exhaustion
            val messageId = entry.msgId
            val scaling = entry.scaling.serializedName
            val effects = entry.effects.serializedName
            val deathMessageType = entry.deathMessageType.serializedName
            values[identifier] = DamageType(identifier, exhaustion, messageId, scaling, effects, deathMessageType)
        }
        this.writeFile { Json.encodeToString(values.values.toList()) }
    }
}

@Serializable
data class DamageType(
    val identifier: String,
    val exhaustion: Float,
    val messageId: String,
    val scaling: String,
    val effects: String,
    val deathMessageType: String,
)
