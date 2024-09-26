package io.github.dockyardmc.registrydatagenerator

import io.netty.buffer.ByteBuf
import net.minecraft.network.FriendlyByteBuf

fun FriendlyByteBuf.write(int: Int) {
    this.writeVarInt(int)
}

fun FriendlyByteBuf.write(float: Float) {
    this.writeFloat(float)
}

fun FriendlyByteBuf.write(string: String) {
    this.writeUtf(string)
}

fun FriendlyByteBuf.write(boolean: Boolean) {
    this.writeBoolean(boolean)
}

fun FriendlyByteBuf.write(list: List<String>) {
    this.writeVarInt(list.size)
    list.forEach(this::writeUtf)
}

fun ByteBuf.toByteArraySafe(): ByteArray {
    if (this.hasArray()) {
        return this.array()
    }

    val bytes = ByteArray(this.readableBytes())
    this.getBytes(this.readerIndex(), bytes)

    return bytes
}

fun FriendlyByteBuf.writeStringIntMap(map: Map<String, Int>) {
    this.writeVarInt(map.size)
    map.forEach {
        this.writeUtf(it.key)
        this.writeVarInt(it.value)
    }
}