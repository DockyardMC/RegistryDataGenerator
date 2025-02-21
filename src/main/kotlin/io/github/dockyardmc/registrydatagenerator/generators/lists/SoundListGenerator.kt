package io.github.dockyardmc.registrydatagenerator.generators.lists

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.BuiltInRegistries
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream

class SoundListGenerator: DataGenerator {

    private val sounds: MutableList<String> = mutableListOf()
    private val listFile = File("./out/classes/Sounds.kt")

    private val file = File("./out/sound_registry.json.gz")

    override fun run() {
        val registry = BuiltInRegistries.SOUND_EVENT
        val contents = buildString {
            append("package io.github.dockyardmc.registry\n")
            append("\n")
            append("object Sounds {\n")
            registry.sortedBy { registry.getId(it) }.forEach { sound ->
                val path = registry.getKey(sound)!!.path
                val variableName = sound.location.path.replace(".", "_").uppercase()

                sounds.add(path)
                append("    const val $variableName = \"${path}\"\n")
            }
            append("}\n")
        }
        listFile.writeText(contents)

        val encodedJson = Json.encodeToString<List<String>>(sounds)
        val compressedData = ByteArrayOutputStream()

        val gzipOutputStream = GZIPOutputStream(compressedData)

        gzipOutputStream.write(encodedJson.toByteArray())
        gzipOutputStream.close()

        file.writeBytes(compressedData.toByteArray())
    }
}