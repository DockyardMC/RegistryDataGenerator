package io.github.dockyardmc.registrydatagenerator

import com.google.common.base.CaseFormat
import com.google.common.base.Optional
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.Registry
import net.minecraft.locale.Language
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.block.state.properties.Property

val language: Language = Language.getInstance()

private fun getCurrentlyRunningServerDedicated(): MinecraftServer {
    return FabricLoader.getInstance().gameInstance as MinecraftServer
}

fun getCurrentlyRunningServer(): MinecraftServer {
    return getCurrentlyRunningServerDedicated()
}

fun getWorld(): ServerLevel {
    return getCurrentlyRunningServer().overworld()
}

fun translate(key: String): String {
    return language.getOrDefault(key)
}

fun <T> Registry<T>.getKeyOrThrow(value: T & Any): ResourceLocation {
    return getKey(value) ?: throw NoSuchElementException("No value present in registry")
}

fun getPropertyTypeName(property: Property<*>): String {
    //Explicitly handle default minecraft properties
    if (property is BooleanProperty) {
        return "bool"
    }
    if (property is IntegerProperty) {
        return "int"
    }
    if (property is EnumProperty) {
        return "enum"
    }

    //Use simple class name as fallback, this code will give something like
    //example_type for ExampleTypeProperty class name
    val rawPropertyName: String = property.javaClass.getSimpleName().replace("Property", "")
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, rawPropertyName)
}