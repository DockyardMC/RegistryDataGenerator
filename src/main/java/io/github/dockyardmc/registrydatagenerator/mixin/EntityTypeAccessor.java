package io.github.dockyardmc.registrydatagenerator.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityType.class)
public interface EntityTypeAccessor {

    @Accessor("immuneTo")
    ImmutableSet<Block> getImmuneTo();

}
