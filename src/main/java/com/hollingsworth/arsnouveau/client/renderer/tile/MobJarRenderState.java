package com.hollingsworth.arsnouveau.client.renderer.tile;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

/**
 * Render state for MobJarRenderer.
 * All values computed in extractRenderState; submit() uses only these fields.
 */
@OnlyIn(Dist.CLIENT)
public class MobJarRenderState extends BlockEntityRenderState {
    /** Extracted entity render state; null if jar is empty. */
    public @Nullable EntityRenderState displayEntity;

    /** Uniform scale applied after JarBehavior adjustments. */
    public float scaleX, scaleY, scaleZ;

    /** Translation within the block (relative to block origin). */
    public float translateX, translateY, translateZ;

    /** Block face the jar is mounted on — drives rotation. */
    public Direction facing = Direction.UP;

    /** Entity bounding-box height — used for DOWN-facing translate. */
    public float bbHeight;
}
