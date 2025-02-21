package io.github.dockyardmc.registrydatagenerator.generators.lists

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import io.github.dockyardmc.registrydatagenerator.getKeyOrThrow
import io.github.dockyardmc.registrydatagenerator.getWorld
import net.minecraft.core.registries.Registries
import java.io.File

class EntityTypeListGenerator: DataGenerator {

    private val entityTypes: MutableList<String> = mutableListOf()
    private val file = File("./out/classes/EntityTypes.kt")

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.ENTITY_TYPE).get()
        val contents = buildString {
            append("package io.github.dockyardmc.registry\n")
            append("\n")
            append("import io.github.dockyardmc.registry.registries.EntityTypeRegistry\n")
            append("\n")
            append("object EntityTypes {\n")
            registry.forEach { entityType ->
                val location = registry.getKeyOrThrow(entityType).path
                val path = "minecraft:${location}"
                val variableName = location.replace(".", "_").uppercase()

                entityTypes.add(path)
                append("    val $variableName = EntityTypeRegistry[\"${path}\"]\n")
            }
            append("}\n")
        }
        file.writeText(contents)
    }
}