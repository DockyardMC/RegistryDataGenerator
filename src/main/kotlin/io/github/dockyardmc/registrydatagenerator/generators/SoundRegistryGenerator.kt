package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import io.github.dockyardmc.registrydatagenerator.getWorld
import net.minecraft.core.registries.Registries
import java.io.File

class SoundRegistryGenerator: DataGenerator {

    private val sounds: MutableList<String> = mutableListOf()
    private val file = File("./out/Sounds.kt")

    override fun run() {
        val registry = getWorld().registryAccess().registry(Registries.SOUND_EVENT).get()
        val contents = buildString {
            append("package io.github.dockyardmc.registry\n")
            append("\n")
            append("object Sounds {\n")
            registry.forEach { sound ->
                val path = "minecraft:${sound.location.path}"
                val variableName = sound.location.path.replace(".", "_").uppercase()

                sounds.add(path)
                append("    const val $variableName = \"${path}\"\n")
            }
            append("}\n")
        }
        file.writeText(contents)
    }
}