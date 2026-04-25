package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

public class ImbuementRenderer extends GeoBlockRenderer<ImbuementTile, ImbuementRenderer.ImbuementRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final RandomSource random = RandomSource.create();

    public static class ImbuementRenderState extends ArsBlockEntityRenderState {
        public @Nullable ItemClusterRenderState displayItem;
        public float rotation;
    }

    public ImbuementRenderer(BlockEntityRendererProvider.Context context) {
        super(new GenericModel<>("imbuement_chamber"));
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public ImbuementRenderState createRenderState() {
        return new ImbuementRenderState();
    }

    @Override
    public void extractRenderState(ImbuementTile tile, ImbuementRenderState state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        super.extractRenderState(tile, state, partialTick, cameraPos, crumbling);
        ItemStack stack = tile.getStack();
        if (stack == null || stack.isEmpty()) {
            state.displayItem = null;
            return;
        }
        state.rotation = partialTick + (float) ClientInfo.ticksInGame;
        state.displayItem = new ItemClusterRenderState();
        itemModelResolver.updateForTopItem(state.displayItem.item, stack, ItemDisplayContext.FIXED,
                tile.getLevel(), null, (int) tile.getBlockPos().asLong());
        state.displayItem.count = 1;
        state.displayItem.seed = 0;
    }

    // ImbuementBlock uses 6-way FACING — suppress auto-rotation by tryRotateByBlockstate (UP-placed blocks flatten)
    @Override
    public void adjustRenderPose(RenderPassInfo<ImbuementRenderState> renderPassInfo) {}

    @Override
    public RenderType getRenderType(ImbuementRenderState renderState, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }

    @Override
    public void submit(ImbuementRenderState renderState, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(renderState, poseStack, collector, cameraState);
        if (renderState.displayItem == null) return;
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.rotation * 3f));
        ItemEntityRenderer.submitMultipleFromCount(poseStack, collector, renderState.lightCoords, renderState.displayItem, random);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() { return true; }

    @Override
    public int getViewDistance() { return 256; }

    @Override
    public AABB getRenderBoundingBox(ImbuementTile blockEntity) { return AABB.INFINITE; }
}
