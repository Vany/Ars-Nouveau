package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

// GeckoLib 5.4.2: GeoBlockRenderer requires R extends BlockEntityRenderState & GeoRenderState.
// ArsBlockEntityRenderState satisfies this at compile time; GeckoLib's mixin does it at runtime.
public abstract class ArsGeoBlockRenderer<T extends BlockEntity & GeoBlockEntity> extends GeoBlockRenderer<T, ArsBlockEntityRenderState> {
    public ArsGeoBlockRenderer(BlockEntityRendererProvider.Context rendererProvider, GeoModel<T> modelProvider) {
        super(modelProvider);
    }

    @Override
    public ArsBlockEntityRenderState createRenderState() {
        return new ArsBlockEntityRenderState();
    }

    /**
     * Shared pose for table-style 2+ block models whose geo origin faces +X (EAST).
     * Rotates +X to align with FACING direction, then shifts 1 block in that direction
     * so the model spans from HEAD block into FOOT block (FOOT is in the FACING direction from HEAD).
     * Called from adjustRenderPose after the non-HEAD guard check.
     */
    public static void applyFacingPose(PoseStack stack, Direction facing) {
        switch (facing) {
            case NORTH -> stack.mulPose(Axis.YP.rotationDegrees(-90));
            case SOUTH -> stack.mulPose(Axis.YP.rotationDegrees(90));
            case EAST  -> stack.mulPose(Axis.YP.rotationDegrees(180));
            default    -> {} // WEST: model already faces west at 0°
        }
        stack.translate(1, 0, 0);
    }
}
