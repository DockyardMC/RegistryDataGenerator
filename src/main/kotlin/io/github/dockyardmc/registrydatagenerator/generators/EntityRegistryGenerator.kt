package io.github.dockyardmc.registrydatagenerator.generators

import io.github.dockyardmc.registrydatagenerator.DataGenerator
import io.github.dockyardmc.registrydatagenerator.getWorld
import io.github.dockyardmc.registrydatagenerator.mixin.EntityAttachmentsAccessor
import io.github.dockyardmc.registrydatagenerator.mixin.EntityTypeAccessor
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityAttachment
import net.minecraft.world.phys.Vec3
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPOutputStream

class EntityRegistryGenerator: DataGenerator {

    val entityTypes = mutableListOf<EntityType>()
    val file = File("./out/entity_type_registry.json.gz")

    override fun run() {
        val registry = getWorld().registryAccess().registry(Registries.ENTITY_TYPE).get()
        val protocolIdCounter = AtomicInteger()

        registry.forEach { entity ->
            val identifier = "minecraft:${registry.getKey(entity)!!.path}"
            val attachmentsAccessor = entity.dimensions.attachments as EntityAttachmentsAccessor

            val nameTagLocation = attachmentsAccessor.attachments[EntityAttachment.NAME_TAG]?.first()?.toDockyardVector3d()
            val passengerLocations = attachmentsAccessor.attachments[EntityAttachment.PASSENGER]?.map { it.toDockyardVector3d() }
            val vehicleLocation = attachmentsAccessor.attachments[EntityAttachment.VEHICLE]?.first()?.toDockyardVector3d()
            val wardenChestLocation = attachmentsAccessor.attachments[EntityAttachment.WARDEN_CHEST]?.first()?.toDockyardVector3d()

            val dimensions = EntityDimensions(
                eyeHeight = entity.dimensions.eyeHeight,
                fixed = entity.dimensions.fixed,
                height = entity.dimensions.height,
                width = entity.dimensions.width,
                nameTagLocation = nameTagLocation,
                passengerLocations = passengerLocations,
                vehicleLocation = vehicleLocation,
                wardenChestLocation = wardenChestLocation,
            )

            val despawnDistance = entity.category.despawnDistance
            val isFriendly = entity.category.isFriendly
            val isPersistent = entity.category.isPersistent
            val maxInstancesPerChunk = entity.category.maxInstancesPerChunk
            val noDespawnDistance = entity.category.noDespawnDistance
            val category = entity.category.name
            val fireImmune = entity.fireImmune()

            val blockRegistry = getWorld().registryAccess().registry(Registries.BLOCK).get()
            val immuneBlocks = (entity as EntityTypeAccessor).immuneTo.map { blockRegistry.getKey(it)!!.path }

            val displayName = entity.description.string

            val protocolId = protocolIdCounter.getAndIncrement()

            val entityType = EntityType(
                identifier = identifier,
                displayName = displayName,
                category = category,
                despawnDistance = despawnDistance,
                isFriendly = isFriendly,
                isPersistent = isPersistent,
                maxInstancesPerChunk = maxInstancesPerChunk,
                noDespawnDistance = noDespawnDistance,
                immuneToFire = fireImmune,
                immuneBlocks = immuneBlocks,
                dimensions = dimensions, protocolId = protocolId
            )
            entityTypes.add(entityType)
        }
        val encodedJson = Json.encodeToString<List<EntityType>>(entityTypes)
        val compressedData = ByteArrayOutputStream()

        val gzipOutputStream = GZIPOutputStream(compressedData)

        gzipOutputStream.write(encodedJson.toByteArray())
        gzipOutputStream.close()

        file.writeBytes(compressedData.toByteArray())
    }
}

@Serializable
data class EntityType(
    val identifier: String,
    val displayName: String,
    val category: String,
    val despawnDistance: Int,
    val isFriendly: Boolean,
    val isPersistent: Boolean,
    val maxInstancesPerChunk: Int,
    val noDespawnDistance: Int,
    val immuneToFire: Boolean,
    val immuneBlocks: List<String>,
    val dimensions: EntityDimensions,
    val protocolId: Int,
)

@Serializable
data class Vector3d(val x: Double, val y: Double, val z: Double)

fun Vec3.toDockyardVector3d(): Vector3d {
    return Vector3d(this.x, this.y, this.z)
}

@Serializable
data class EntityDimensions(
    val eyeHeight: Float,
    val fixed: Boolean,
    val height: Float,
    val width: Float,
    val nameTagLocation: Vector3d?,
    val passengerLocations: List<Vector3d>?,
    val vehicleLocation: Vector3d?,
    val wardenChestLocation: Vector3d?
)