package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.api.registry.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MobJarRenderer implements BlockEntityRenderer<MobJarTile, MobJarRenderState> {
    private final EntityRenderDispatcher entityRenderer;

    public MobJarRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.entityRenderer();
    }

    @Override
    public MobJarRenderState createRenderState() {
        return new MobJarRenderState();
    }

    @Override
    public void extractRenderState(
        MobJarTile tile, MobJarRenderState state, float partialTick, Vec3 cameraPos,
        ModelFeatureRenderer.@Nullable CrumblingOverlay overlay
    ) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTick, cameraPos, overlay);
        state.displayEntity = null;

        Entity entity = tile.getEntity();
        if (entity == null) return;

        // Extract entity render state (equivalent to SpawnerRenderer pattern)
        state.displayEntity = entityRenderer.extractEntity(entity, partialTick);
        state.displayEntity.lightCoords = state.lightCoords;

        // Base scale: fit entity inside the jar's ~0.53 block interior
        float scale = entity instanceof LightningBolt ? 0.0075f : 0.53125f;
        float maxDim = Math.max(entity.getBbWidth(), entity.getBbHeight());
        if (maxDim > 1.0f) scale /= maxDim;
        state.bbHeight = entity.getBbHeight();

        // JarBehavior adjustments (scale multiplier and translation offset)
        AtomicReference<Vec3> scaleAdj = new AtomicReference<>(Vec3.ZERO);
        AtomicReference<Vec3> translateAdj = new AtomicReference<>(Vec3.ZERO);
        JarBehaviorRegistry.forEach(entity, behavior -> {
            scaleAdj.set(scaleAdj.get().add(behavior.scaleOffset(tile)));
            translateAdj.set(translateAdj.get().add(behavior.translate(tile)));
        });

        Vec3 finalScale = new Vec3(scale, scale, scale).multiply(scaleAdj.get().add(1, 1, 1));
        Vec3 finalTranslate = new Vec3(0.5, 0, 0.5).add(translateAdj.get());

        state.scaleX = (float) finalScale.x;
        state.scaleY = (float) finalScale.y;
        state.scaleZ = (float) finalScale.z;
        state.translateX = (float) finalTranslate.x;
        state.translateY = (float) finalTranslate.y;
        state.translateZ = (float) finalTranslate.z;
        state.facing = tile.getBlockState().getValue(MobJar.FACING);
    }

    @Override
    public boolean shouldRender(MobJarTile blockEntity, Vec3 cameraPos) {
        return blockEntity.isVisible && BlockEntityRenderer.super.shouldRender(blockEntity, cameraPos);
    }

    @Override
    public void submit(
        MobJarRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState
    ) {
        if (state.displayEntity == null) return;

        poseStack.pushPose();
        poseStack.translate(state.translateX, state.translateY, state.translateZ);
        poseStack.scale(state.scaleX, state.scaleY, state.scaleZ);

        // Rotate entity based on which face the jar is mounted on
        Direction facing = state.facing;
        if (facing == Direction.EAST) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        } else if (facing == Direction.WEST) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
        } else if (facing == Direction.NORTH) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        } else if (facing == Direction.SOUTH) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        } else if (facing == Direction.DOWN) {
            poseStack.translate(0, state.bbHeight + 0.75f, 0);
        }
        poseStack.mulPose(facing.getRotation());

        // x/y/z = 0: PoseStack already positions us at block origin via the BER infrastructure.
        // EntityRenderDispatcher.submit will add getRenderOffset on top, which is correct.
        entityRenderer.submit(state.displayEntity, cameraState, 0.0, 0.0, 0.0, poseStack, collector);

        poseStack.popPose();
    }
}
