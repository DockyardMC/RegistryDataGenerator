package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import net.minecraft.core.registries.BuiltInRegistries
import java.io.File

class ParticleListGenerator: DataGenerator {

    private val sounds: MutableList<String> = mutableListOf()
    private val listFile = File("./out/Particles.kt")

    override fun run() {
        val registry = BuiltInRegistries.PARTICLE_TYPE
        val contents = buildString {
            append("package io.github.dockyardmc.registry\n")
            append("\n")
            append("import io.github.dockyardmc.registry.registries.ParticleRegistry\n")
            append("\n")
            append("object Particles {\n")
            registry.sortedBy { registry.getId(it) }.forEach { particle ->
                val path = registry.getKey(particle)!!.path
                val variableName = path.replace(".", "_").uppercase()

                sounds.add(path)
                append("    val $variableName = ParticleRegistry[\"minecraft:${path}\"]\n")
            }
            append("}\n")
        }
        listFile.writeText(contents)
    }
}