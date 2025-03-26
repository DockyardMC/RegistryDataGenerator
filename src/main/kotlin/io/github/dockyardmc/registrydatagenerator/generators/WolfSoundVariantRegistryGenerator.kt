package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class WolfSoundVariantRegistryGenerator : AbstractDataGenerator<WolfSoundVariant>("wolf_sound_variant", "WolfSoundVariants", "WolfSoundVariantRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.WOLF_SOUND_VARIANT).get()

        registry.forEach { wolfVariant ->
            val id = registry.getKey(wolfVariant)!!.path
            values[id] = WolfSoundVariant(
                ambientSound = wolfVariant.ambientSound.registeredName,
                deathSound = wolfVariant.deathSound.registeredName,
                growlSound = wolfVariant.growlSound.registeredName,
                hurtSound = wolfVariant.hurtSound.registeredName,
                pantSound = wolfVariant.pantSound.registeredName,
                whineSound = wolfVariant.whineSound.registeredName,
            )
        }
        writeFile { Json.encodeToString<List<WolfSoundVariant>>(values.values.toList()) }
    }
}

@Serializable
data class WolfSoundVariant(
    val ambientSound: String,
    val deathSound: String,
    val growlSound: String,
    val hurtSound: String,
    val pantSound: String,
    val whineSound: String,
)