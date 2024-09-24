package io.github.dockyardmc.registrydatagenerator.generators

import getKeyOrThrow
import getPropertyTypeName
import getWorld
import io.github.dockyardmc.registrydatagenerator.DataGenerator
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.EmptyBlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BooleanProperty
import translate
import java.io.File

class BlockRegistryGenerator : DataGenerator {

    val blocks = mutableListOf<RegistryBlock>()
    val file = File("./out/blocks.json")

    override fun run() {
        val blockRegistry = getWorld().registryAccess().registry(Registries.BLOCK)
        blockRegistry.get().forEach { block ->
            val identifier = "minecraft:${blockRegistry.get().getKeyOrThrow(block).path}"
            val defaultBlockState = block.defaultBlockState()
            val displayName = translate(block.descriptionId)
            val explosionResistance = block.explosionResistance
            val destroyTime = block.defaultDestroyTime()
            val isSignalSource = defaultBlockState.isSignalSource
            val lightEmission = defaultBlockState.lightEmission
            val sounds = RegistryBlockSounds(
                breakSound = "minecraft:${defaultBlockState.soundType.breakSound.location.path}",
                hitSound = "minecraft:${defaultBlockState.soundType.breakSound.location.path}",
                placeSound = "minecraft:${defaultBlockState.soundType.breakSound.location.path}",
                fallSound = "minecraft:${defaultBlockState.soundType.breakSound.location.path}",
                walkSound = "minecraft:${defaultBlockState.soundType.breakSound.location.path}",
            )
            val isTransparent = !defaultBlockState.isSolid
            val renderShape = defaultBlockState.renderShape.name
            val isBlockEntity = defaultBlockState.hasBlockEntity()
            val lightFilter = defaultBlockState.getLightBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)
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

            val possibleBlockStates = mutableListOf<RegistryBlockState>()
            properties.forEach { property ->
                val name = property.getName()
                val type = getPropertyTypeName(property)
                val values = mutableListOf<String>()
                if(property !is BooleanProperty) {
                    property.possibleValues.forEach {
                        values.add(it.toString())
                    }
                }
                val valuesArray = if(values.isEmpty()) null else values
                possibleBlockStates.add(

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
                isTransparent = isTransparent,
                renderShape = renderShape,
                isBlockEntity = isBlockEntity,
                lightFilter = lightFilter,
                isAir = isAir,
                isSolid = isSolid,
                isLiquid = isLiquid,
                isFlammable = isFlammable,
                canOcclude = canOcclude,
                replaceable = replaceable,
                states = possibleBlockStates,
                defaultBlockStateId = defaultBlockStateId,
                minBlockStateId = minBlockStateId,
                maxBlockStateId = maxBlockStateId,
                sounds = sounds
            )
            blocks.add(registryBlock)
        }
        file.writeText(Json.encodeToString<List<RegistryBlock>>(blocks))
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
    val isTransparent: Boolean,
    val renderShape: String,
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
    val sounds: RegistryBlockSounds
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
    val values: List<String>? = null
)