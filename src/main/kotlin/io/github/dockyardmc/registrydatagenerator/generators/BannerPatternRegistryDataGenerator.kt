package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries

class BannerPatternRegistryDataGenerator : AbstractDataGenerator<BannerPattern>("banner_pattern_registry", "BannerPatterns", "BannerPatternRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.BANNER_PATTERN).get()

        registry.forEach { entry ->
            val identifier = registry.getKey(entry).toString()
            values[identifier] = BannerPattern(entry.translationKey.toString(), entry.assetId.toString())
        }
        this.writeFile { Json.encodeToString<List<BannerPattern>>(values.values.toList()) }
    }
}

@Serializable
data class BannerPattern(
    val identifier: String,
    val translationKey: String,
)