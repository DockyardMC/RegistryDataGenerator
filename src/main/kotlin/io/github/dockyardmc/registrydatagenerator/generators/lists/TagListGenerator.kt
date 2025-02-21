package io.github.dockyardmc.registrydatagenerator.generators.lists

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import io.github.dockyardmc.registrydatagenerator.generators.TagRegistryGenerator
import java.io.File

class TagListGenerator : DataGenerator {
    private val listFile = File("./out/classes/Tags.kt")

    val stringBuilder = StringBuilder()

    override fun run() {
        stringBuilder.append("package io.github.dockyardmc.registry\n")
        stringBuilder.append("\n")
        stringBuilder.append("import io.github.dockyardmc.registry.registries.tags.*\n")
        stringBuilder.append("\n")
        stringBuilder.append("object Tags {\n")

        TagRegistryGenerator.registries.forEach { registry ->
            val name = registry.key.key().location().toString().replace("minecraft:", "")
            val variablePrefix = (if(name.contains("/")) name.split("/")[1] else name).uppercase()

            TagRegistryGenerator.getTags(registry.key).forEach { tag ->
                stringBuilder.append("    val ${variablePrefix}_${tag.identifier.replace("minecraft:", "").replace("/", "_").uppercase()} = ${registry.value}TagRegistry[\"${tag.identifier}\"]\n")
            }
        }

        stringBuilder.append("}\n")

        listFile.writeText(stringBuilder.toString())

    }
}