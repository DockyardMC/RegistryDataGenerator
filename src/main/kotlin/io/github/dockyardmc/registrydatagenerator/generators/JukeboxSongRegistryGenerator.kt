package io.github.dockyardmc.registrydatagenerator.generators

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import io.github.dockyardmc.registrydatagenerator.getWorld
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization

class JukeboxSongRegistryGenerator : AbstractDataGenerator<JukeboxSong>("jukebox_song_registry", "JukeboxSongs", "JukeboxSongRegistry") {

    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.JUKEBOX_SONG).get()
        registry.forEach { entry ->
            val identifier = registry.getKey(entry).toString()
            val comparatorOutput = entry.comparatorOutput
            val description = entry.description
            val lengthInSeconds = entry.lengthInSeconds
            val sound = entry.soundEvent.unwrapKey().get().toString()
            values[identifier] = JukeboxSong(identifier, comparatorOutput, description, lengthInSeconds, sound)
        }
        writeFile { Json.encodeToString(values.values.toList()) }
    }
}

@Serializable
data class JukeboxSong(
    val identifier: String,
    val comparatorOutput: Int,
    @Serializable(ComponentToJsonSerializer::class)
    val description: Component,
    val lengthInSeconds: Float,
    val sound: String,
)

object ComponentToJsonSerializer : KSerializer<Component> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("component", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Component {
        val string = decoder.decodeString()
        val result = ComponentSerialization.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(string))
        return result.getOrThrow().first
    }

    override fun serialize(encoder: Encoder, value: Component) {
        val result = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, value)
        encoder.encodeString(result.getOrThrow().toString())
    }

}