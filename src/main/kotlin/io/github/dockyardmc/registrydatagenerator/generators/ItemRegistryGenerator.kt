package io.github.dockyardmc.registrydatagenerator.generators

import cz.lukynka.prettylog.log
import io.github.dockyardmc.registrydatagenerator.DataGenerator
import io.github.dockyardmc.registrydatagenerator.getWorld
import io.netty.buffer.Unpooled
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.TypedDataComponent
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.enchantment.ItemEnchantments
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset
import java.util.zip.GZIPOutputStream
import kotlin.io.encoding.ExperimentalEncodingApi

class ItemRegistryGenerator : DataGenerator {

    private val items: MutableList<Item> = mutableListOf()
    private val file = File("./out/item_registry.json.gz")

    @OptIn(ExperimentalEncodingApi::class)
    override fun run() {
        val registry = getWorld().registryAccess().lookup(Registries.ITEM).get()
        val blockRegistry = getWorld().registryAccess().lookup(Registries.BLOCK).get()
        val componentRegistry = getWorld().registryAccess().lookup(Registries.DATA_COMPONENT_TYPE).get()

        val blocks = blockRegistry.map { "minecraft:${blockRegistry.getKey(it)!!.path}" }

        registry.forEach { item ->
            val identifier = "minecraft:${registry.getKey(item)!!.path}"
            val displayName = item.defaultInstance.displayName.string
            val maxStack = item.defaultMaxStackSize
            val consumeSound = getConsumeSound(item)
            val canFitInsideContainers = item.canFitInsideContainerItems()
            val isEnchantable = item.defaultInstance.isEnchantable
            val isStackable = item.defaultInstance.isStackable
            val isDamageable = item.defaultInstance.isDamageableItem
            val isBlock = blocks.contains(identifier)

            val components = item.components()
            val encodedComponents: MutableMap<Int, String> = mutableMapOf()
            log(" ")
            log("$identifier:")
            components.forEach componentLoop@{ component ->
                when (component.type.toString()) {
                    "minecraft:max_stack_size" -> {
                        val maxStackSize = component.value as Int
                        if (maxStackSize == 64) return@componentLoop
                    }

                    "minecraft:attribute_modifiers" -> {
                        val attributes = component.value as ItemAttributeModifiers
                        if (attributes.modifiers.isEmpty()) return@componentLoop
                    }

                    "minecraft:enchantments" -> {
                        val enchantments = component.value as ItemEnchantments
                        if (enchantments.isEmpty) return@componentLoop
                    }

                    "minecraft:lore" -> {
                        val lore = component.value as ItemLore
                        if (lore.lines.size == 0) return@componentLoop
                    }

                    "minecraft:repair_cost" -> {
                        val repairCost = component.value as Int
                        if (repairCost == 0) return@componentLoop
                    }

                    "minecraft:rarity" -> {
                        val rarity = component.value as Rarity
                        if (rarity == Rarity.COMMON) return@componentLoop
                    }
                }

                val protocolId = componentRegistry.getId(component.type)

                val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), getWorld().registryAccess())
                TypedDataComponent.STREAM_CODEC.encode(buffer, component)
                val base64Encoded = buffer.toString(Charset.defaultCharset())
                encodedComponents[protocolId] = base64Encoded

                log("- ${component.type}: $base64Encoded (${buffer.readableBytes()})")
            }
            val registryItem = Item(
                identifier,
                displayName,
                maxStack,
                consumeSound,
                canFitInsideContainers,
                isEnchantable,
                isStackable,
                isDamageable,
                isBlock,
                encodedComponents
            )
            items.add(registryItem)

            log(" ")
        }
        val encodedJson = Json.encodeToString<List<Item>>(items)
        val compressedData = ByteArrayOutputStream()

        val gzipOutputStream = GZIPOutputStream(compressedData)

        gzipOutputStream.write(encodedJson.toByteArray())
        gzipOutputStream.close()

        file.writeBytes(compressedData.toByteArray())
    }

    private fun getConsumeSound(item: net.minecraft.world.item.Item): String {
        val defaultInstance = item.defaultInstance
        val consumable = defaultInstance[DataComponents.CONSUMABLE] ?: return "minecraft:entity.generic.eat"
        return "minecraft:${consumable.sound.value().location.path}"
    }
}

@Serializable
data class Item(
    val identifier: String,
    val displayName: String,
    val maxStack: Int,
    val consumeSound: String,
    val canFitInsideContainers: Boolean,
    val isEnchantable: Boolean,
    val isStackable: Boolean,
    val isDamageable: Boolean,
    val isBlock: Boolean,
    val encodedComponents: MutableMap<Int, String>
)