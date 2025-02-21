package io.github.dockyardmc.registrydatagenerator.generators.lists

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import io.github.dockyardmc.registrydatagenerator.getKeyOrThrow
import io.github.dockyardmc.registrydatagenerator.getWorld
import net.minecraft.core.registries.Registries
import java.io.File

class BlockListGenerator: DataGenerator {

    private val blocks: MutableList<String> = mutableListOf()
    private val file = File("./out/classes/Blocks.kt")

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.BLOCK).get()
        val contents = buildString {
            append("package io.github.dockyardmc.registry\n")
            append("\n")
            append("import io.github.dockyardmc.registry.registries.BlockRegistry\n")
            append("\n")
            append("object Blocks {\n")
            registry.forEach { block ->
                val location = registry.getKeyOrThrow(block).path
                val path = "minecraft:${location}"
                val variableName = location.replace(".", "_").uppercase()

                blocks.add(path)
                append("    val $variableName = BlockRegistry[\"${path}\"]\n")
            }
            append("}\n")
        }
        file.writeText(contents)
    }
}