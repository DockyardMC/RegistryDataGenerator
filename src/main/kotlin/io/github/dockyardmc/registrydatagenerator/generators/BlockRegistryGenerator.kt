package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.EmptyBlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.shapes.CollisionContext
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

            val breakSpeed: Float = defaultBlockState.getDestroySpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)
            val requiresToolToBreak = defaultBlockState.requiresCorrectToolForDrops()

            val isBlockEntity = defaultBlockState.hasBlockEntity()
            val blockEntityId = if(isBlockEntity) {
                var blockEntityType = BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(blockRegistry.getKeyOrThrow(block))
                if(identifier.contains("shulker_box")) blockEntityType = BlockEntityType.SHULKER_BOX
                if(identifier.contains("bed")) blockEntityType = BlockEntityType.BED
                if(identifier.contains("banner")) blockEntityType = BlockEntityType.BANNER
                if(identifier.contains("campfire")) blockEntityType = BlockEntityType.CAMPFIRE
                if(identifier.contains("hanging_sign")) blockEntityType = BlockEntityType.HANGING_SIGN
                if(identifier.contains("sign")) blockEntityType = BlockEntityType.SIGN
                if(identifier.contains("piston")) blockEntityType = BlockEntityType.PISTON
                if(identifier.contains("command_block")) blockEntityType = BlockEntityType.COMMAND_BLOCK
                BuiltInRegistries.BLOCK_ENTITY_TYPE.getId(blockEntityType)
            } else null

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
            val shapes = mutableMapOf<Int, String>()
            val collisionShape = mutableMapOf<Int, String>()
            val interactionShape = mutableMapOf<Int, String>()
            val occlusionShape = mutableMapOf<Int, String>()
            val visualShape = mutableMapOf<Int, String>()

            val blockStateList = mutableListOf<RegistryBlockState>()
            states.forEach {
                val stateId: Int = Block.BLOCK_STATE_REGISTRY.getId(it)
                val stateString: String = it.toString().replace("Block{", "").replace("}", "")
                possibleBlockStates[stateString] = stateId
                shapes[stateId] = it.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString()
                collisionShape[stateId] = it.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString()
                interactionShape[stateId] = it.getInteractionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString()
                occlusionShape[stateId] = it.occlusionShape.toAabbs().toString()
                visualShape[stateId] = it.getVisualShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty()).toAabbs().toString()
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
                    RegistryBlockState(
                        name,
                        type,
                        valuesArray

                    )
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
                blockEntityId = blockEntityId,
                lightFilter = lightFilter,
                isAir = isAir,
                isSolid = isSolid,
                isLiquid = isLiquid,
                isFlammable = isFlammable,
                breakSpeed = breakSpeed,
                requiresToolToBreak = requiresToolToBreak,
                canOcclude = canOcclude,
                replaceable = replaceable,
                states = blockStateList,
                defaultBlockStateId = defaultBlockStateId,
                minBlockStateId = minBlockStateId,
                maxBlockStateId = maxBlockStateId,
                sounds = sounds,
                tags = tags,
                possibleStates = possibleBlockStates,
                shape = shapes,
                collisionShape = collisionShape,
                interactionShape = interactionShape,
                occlusionShape = occlusionShape,
                visualShape = visualShape,
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
    val blockEntityId: Int?,
    val lightFilter: Int,
    val isAir: Boolean,
    val isSolid: Boolean,
    val isLiquid: Boolean,
    val isFlammable: Boolean,
    val breakSpeed: Float,
    val requiresToolToBreak: Boolean,
    val canOcclude: Boolean,
    val replaceable: Boolean,
    val states: List<RegistryBlockState>,
    val defaultBlockStateId: Int,
    val minBlockStateId: Int,
    val maxBlockStateId: Int,
    val sounds: RegistryBlockSounds,
    val tags: List<String>,
    val possibleStates: Map<String, Int>,
    val shape: Map<Int, String>,
    val collisionShape: Map<Int, String>,
    val interactionShape: Map<Int, String>,
    val occlusionShape: Map<Int, String>,
    val visualShape: Map<Int, String>,
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