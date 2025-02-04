package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import io.github.dockyardmc.registrydatagenerator.getKeyOrThrow
import io.github.dockyardmc.registrydatagenerator.getWorld
import net.minecraft.core.registries.Registries
import java.io.File

class ItemListGenerator: DataGenerator {

    private val blocks: MutableList<String> = mutableListOf()
    private val file = File("./out/Items.kt")

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.ITEM).get()
        val contents = buildString {
            append("package io.github.dockyardmc.registry\n")
            append("\n")
            append("import io.github.dockyardmc.registry.registries.ItemRegistry\n")
            append("\n")
            append("object Items {\n")
            registry.forEach { item ->
                val location = registry.getKeyOrThrow(item).path
                val path = "minecraft:${location}"
                val variableName = location.replace(".", "_").uppercase()

                blocks.add(path)
                append("    val $variableName = ItemRegistry[\"${path}\"]\n")
            }
            append("}\n")
        }
        file.writeText(contents)
    }
}