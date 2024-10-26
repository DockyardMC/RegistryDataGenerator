package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BooleanProperty
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream

class BlockRegistryGenerator : DataGenerator {

    val blocks = mutableListOf<RegistryBlock>()
    val file = File("./out/block_registry.json.gz")

    override fun run() {
        val blockRegistry = getWorld().registryAccess().lookup(Registries.BLOCK).get()
        blockRegistry.forEach { block ->
            val identifier = "minecraft:${blockRegistry.getKeyOrThrow(block).path}"
            val defaultBlockState = block.defaultBlockState()
            val displayName = translate(block.descriptionId)
            val explosionResistance = block.explosionResistance
            val destroyTime = block.defaultDestroyTime()
            val isSignalSource = defaultBlockState.isSignalSource
            val lightEmission = defaultBlockState.lightEmission
            val sounds = RegistryBlockSounds(
                breakSound = "minecraft:${defaultBlockState.soundType.breakSound.location.path}",
                hitSound = "minecraft:${defaultBlockState.soundType.hitSound.location.path}",
                placeSound = "minecraft:${defaultBlockState.soundType.placeSound.location.path}",
                fallSound = "minecraft:${defaultBlockState.soundType.fallSound.location.path}",
                walkSound = "minecraft:${defaultBlockState.soundType.stepSound.location.path}",
            )
            val isBlockEntity = defaultBlockState.hasBlockEntity()
            val lightFilter = defaultBlockState.lightBlock
            val isAir = defaultBlockState.isAir
            val isSolid = defaultBlockState.isSolid
            val isLiquid = defaultBlockState.liquid()
            val isFlammable = defaultBlockState.ignitedByLava()
            val canOcclude = defaultBlockState.canOcclude()
            val replaceable = defaultBlockState.canBeReplaced()

            val states = block.stateDefinition.possibleStates
            val properties = block.stateDefinition.properties
            val defaultBlockStateId = Block.getId(defaultBlockState)
            val minBlockStateId = Block.getId(states.first())
            val maxBlockStateId = Block.getId(states.last())

            val tags = defaultBlockState.tags.map { "minecraft:${it.location.path}" }.toList()

            val possibleBlockStates = mutableMapOf<String, Int>()
            val blockStateList = mutableListOf<RegistryBlockState>()
            states.forEach {
                val stateId: Int = Block.BLOCK_STATE_REGISTRY.getId(it)
                val stateString: String = it.toString().replace("Block{", "").replace("}", "")
                possibleBlockStates[stateString] = stateId
            }

            properties.forEach { property ->
                val name = property.name
                val type = getPropertyTypeName(property)
                val values = mutableListOf<String>()
                if (property !is BooleanProperty) {
                    property.possibleValues.forEach {
                        values.add(it.toString())
                    }
                }
                val valuesArray = if (values.isEmpty()) null else values
                blockStateList.add(
                    RegistryBlockState(name, type, valuesArray)
                )
            }

            val registryBlock = RegistryBlock(
                identifier = identifier,
                displayName = displayName,
                explosionResistance = explosionResistance,
                destroyTime = destroyTime,
                isSignalSource = isSignalSource,
                lightEmission = lightEmission,
                isBlockEntity = isBlockEntity,
                lightFilter = lightFilter,
                isAir = isAir,
                isSolid = isSolid,
                isLiquid = isLiquid,
                isFlammable = isFlammable,
                canOcclude = canOcclude,
                replaceable = replaceable,
                states = blockStateList,
                defaultBlockStateId = defaultBlockStateId,
                minBlockStateId = minBlockStateId,
                maxBlockStateId = maxBlockStateId,
                sounds = sounds,
                tags = tags,
                possibleStates = possibleBlockStates
            )
            blocks.add(registryBlock)
        }

        val encodedJson = Json.encodeToString<List<RegistryBlock>>(blocks)
        val compressedData = ByteArrayOutputStream()

        val gzipOutputStream = GZIPOutputStream(compressedData)

        gzipOutputStream.write(encodedJson.toByteArray())
        gzipOutputStream.close()

        file.writeBytes(compressedData.toByteArray())
    }
}

@Serializable
data class RegistryBlock(
    val identifier: String,
    val displayName: String,
    val explosionResistance: Float,
    val destroyTime: Float,
    val isSignalSource: Boolean,
    val lightEmission: Int,
    val isBlockEntity: Boolean,
    val lightFilter: Int,
    val isAir: Boolean,
    val isSolid: Boolean,
    val isLiquid: Boolean,
    val isFlammable: Boolean,
    val canOcclude: Boolean,
    val replaceable: Boolean,
    val states: List<RegistryBlockState>,
    val defaultBlockStateId: Int,
    val minBlockStateId: Int,
    val maxBlockStateId: Int,
    val sounds: RegistryBlockSounds,
    val tags: List<String>,
    val possibleStates: Map<String, Int>,
)

@Serializable
data class RegistryBlockSounds(
    val breakSound: String,
    val hitSound: String,
    val placeSound: String,
    val fallSound: String,
    val walkSound: String,
)

@Serializable
data class RegistryBlockState(
    val name: String,
    val type: String,
    val values: List<String>? = null,
)