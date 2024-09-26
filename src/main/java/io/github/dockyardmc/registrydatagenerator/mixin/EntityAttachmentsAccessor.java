package io.github.dockyardmc.registrydatagenerator.mixin;

import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(EntityAttachments.class)
public interface EntityAttachmentsAccessor {

    @Accessor
    Map<EntityAttachment, List<Vec3>> getAttachments();
}
