package io.github.dockyardmc.registrydatagenerator.generators.lists

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import io.github.dockyardmc.registrydatagenerator.getKeyOrThrow
import io.github.dockyardmc.registrydatagenerator.getWorld
import net.minecraft.core.registries.Registries
import java.io.File

class AttributeListGenerator: DataGenerator {

    private val attributes: MutableList<String> = mutableListOf()
    private val file = File("./out/classes/Attributes.kt")

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.ATTRIBUTE).get()
        val contents = buildString {
            append("package io.github.dockyardmc.registry\n")
            append("\n")
            append("import io.github.dockyardmc.registry.registries.AttributeRegistry\n")
            append("\n")
            append("object Attributes {\n")
            registry.forEach { entityType ->
                val location = registry.getKeyOrThrow(entityType).path
                val path = "minecraft:${location}"
                val variableName = location.replace(".", "_").uppercase()

                attributes.add(path)
                append("    val $variableName = AttributeRegistry[\"${path}\"]\n")
            }
            append("}\n")
        }
        file.writeText(contents)
    }
}