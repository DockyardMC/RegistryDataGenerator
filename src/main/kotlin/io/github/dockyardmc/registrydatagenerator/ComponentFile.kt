package io.github.dockyardmc.registrydatagenerator

import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import java.io.File

class ComponentFile(val file: File, val components: MutableMap<String, MutableMap<Int, FriendlyByteBuf>>) {

    val buffer: FriendlyByteBuf = FriendlyByteBuf(Unpooled.buffer())

    fun write() {
        buffer.writeVarInt(components.size)
        components.forEach { (identifier, map) ->
            buffer.writeUtf(identifier)
            buffer.writeVarInt(map.size)
            map.forEach { (id, component) ->
                val size = component.readableBytes()
                buffer.writeVarInt(id)
                buffer.writeVarInt(size)
                buffer.writeBytes(component)
            }
        }

        file.createNewFile()
        file.writeBytes(buffer.array())
    }

    companion object {
        fun read(buffer: FriendlyByteBuf) {
            val size = buffer.readVarInt()
            for (i in 0 until size) {
                val identifier = buffer.readUtf()
                val mapSize = buffer.readVarInt()
                for (i1 in 0 until mapSize) {
                    val componentId = buffer.readVarInt()
                    val length = buffer.readVarInt()
                    val component = buffer.readBytes(length)
                }
            }
        }
    }
}