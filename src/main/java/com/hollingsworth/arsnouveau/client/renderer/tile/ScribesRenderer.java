package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.block.ThreePartBlock;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.ArrayList;
import java.util.List;

public class ScribesRenderer extends GeoBlockRenderer<ScribesTile, ScribesRenderer.ScribesRenderState> {
    public static GeoModel model = new GenericModel<>("scribes_table");
    private final ItemModelResolver itemModelResolver;
    private final RandomSource random = RandomSource.create();

    public static class ScribesRenderState extends ArsBlockEntityRenderState {
        // Item pressed flat on the table surface (crafting target, blank glyph, or held item)
        public @Nullable ItemClusterRenderState pressedItem;
        // Remaining required ingredients orbiting above the table
        public List<ItemClusterRenderState> orbitItems = new ArrayList<>();
        public Direction facing;
        public float ticks;
        public Vec3 orbitTranslation = Vec3.ZERO;
    }

    public ScribesRenderer(BlockEntityRendererProvider.Context context) {
        super(model);
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public ScribesRenderState createRenderState() {
        return new ScribesRenderState();
    }

    @Override
    public void extractRenderState(ScribesTile tile, ScribesRenderState state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        super.extractRenderState(tile, state, partialTick, cameraPos, crumbling);
        state.pressedItem = null;
        state.orbitItems.clear();
        BlockState blockState = state.blockState;
        if (blockState.getBlock() != BlockRegistry.SCRIBES_BLOCK.get()) return;
        if (blockState.getValue(ScribesBlock.PART) != ThreePartBlock.HEAD) return;

        state.facing = blockState.getValue(ScribesBlock.FACING);
        state.ticks = partialTick + (float) ClientInfo.ticksInGame;
        state.orbitTranslation = orbitTranslation(state.facing);

        // Item on table: output if near-done, blank glyph if crafting, else the held item
        ItemStack pressed;
        if (tile.crafting && tile.craftingTicks < 40 && tile.recipe != null) {
            pressed = tile.recipe.value().output.copy();
        } else if (tile.crafting) {
            pressed = ItemsRegistry.BLANK_GLYPH.get().getDefaultInstance();
        } else {
            pressed = tile.getStack();
        }
        if (pressed != null && !pressed.isEmpty()) {
            state.pressedItem = new ItemClusterRenderState();
            itemModelResolver.updateForTopItem(state.pressedItem.item, pressed, ItemDisplayContext.FIXED,
                    tile.getLevel(), null, (int) tile.getBlockPos().asLong());
            state.pressedItem.count = 1;
            state.pressedItem.seed = 0;
        }

        // Orbiting items: remaining required ingredients when recipe set but crafting not yet started
        if (tile.recipe != null && !tile.crafting) {
            for (Ingredient ingredient : tile.getRemainingRequired()) {
                List<ItemStack> choices = ingredient.items()
                        .map(h -> h.value().getDefaultInstance()).toList();
                if (choices.isEmpty()) continue;
                ItemStack choice = choices.get((ClientInfo.ticksInGame / 20) % choices.size());
                ItemClusterRenderState orbitState = new ItemClusterRenderState();
                itemModelResolver.updateForTopItem(orbitState.item, choice, ItemDisplayContext.FIXED,
                        tile.getLevel(), null, (int) tile.getBlockPos().asLong());
                orbitState.count = 1;
                orbitState.seed = 0;
                state.orbitItems.add(orbitState);
            }
        }
    }

    private static Vec3 orbitTranslation(Direction facing) {
        return switch (facing) {
            case WEST  -> new Vec3(1,   0, 0.5);
            case EAST  -> new Vec3(0,   0, 0.5);
            case SOUTH -> new Vec3(0.5, 0, 0);
            case NORTH -> new Vec3(0.5, 0, 1);
            default    -> Vec3.ZERO;
        };
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<ScribesRenderState> renderPassInfo) {
        // Do NOT call super — tryRotateByBlockstate would double-rotate with our custom code below
        BlockState state = renderPassInfo.renderState().blockState;
        if (state.getBlock() != BlockRegistry.SCRIBES_BLOCK.get()) return;
        if (state.getValue(ScribesBlock.PART) != ThreePartBlock.HEAD) return;
        Direction direction = state.getValue(ScribesBlock.FACING);
        PoseStack stack = renderPassInfo.poseStack();
        ArsGeoBlockRenderer.applyFacingPose(stack, direction);
    }

    @Override
    public RenderType getRenderType(ScribesRenderState renderState, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }

    // Skip non-HEAD parts: ScribesBlock is a 2-part block (HEAD+FOOT), each with its own tile entity.
    // Item rendering (pressed item + orbiting ingredients) only fires from HEAD.
    @Override
    public void submit(ScribesRenderState renderState, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState) {
        BlockState state = renderState.blockState;
        if (state.getBlock() == BlockRegistry.SCRIBES_BLOCK.get()
                && state.getValue(ScribesBlock.PART) != ThreePartBlock.HEAD) return;
        super.submit(renderState, poseStack, collector, cameraState);
        if (renderState.pressedItem != null) renderPressedItem(renderState, poseStack, collector);
        if (!renderState.orbitItems.isEmpty()) renderOrbitItems(renderState, poseStack, collector);
    }

    private void renderPressedItem(ScribesRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();
        poseStack.translate(0, 1.0, 0);
        Direction direction = state.facing;
        Quaternionf quat = switch (direction) {
            case EAST, SOUTH -> Axis.ZP.rotationDegrees(180f);
            default          -> Axis.ZP.rotationDegrees(90f);
        };
        switch (direction) {
            case WEST  -> poseStack.translate(1, 0, 0.5f);
            case EAST  -> poseStack.translate(0, 0, 0.5f);
            case SOUTH -> poseStack.translate(0.5f, 0, 0);
            case NORTH -> poseStack.translate(0.5f, 0, 1);
            default    -> {}
        }
        float y = direction.getClockWise().toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(-y + 90f));
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
        poseStack.mulPose(quat);
        poseStack.scale(0.6f, 0.6f, 0.3f);
        ItemEntityRenderer.submitMultipleFromCount(poseStack, collector, state.lightCoords, state.pressedItem, random);
        poseStack.popPose();
    }

    private void renderOrbitItems(ScribesRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        int size = state.orbitItems.size();
        float angleBetweenEach = 360.0f / size;
        Vec3 distanceVec = new Vec3(1, -0.5, 1);
        Vec3 t = state.orbitTranslation;
        float ticks = state.ticks;
        for (int i = 0; i < size; i++) {
            poseStack.pushPose();
            poseStack.translate(0, 2, 0);
            poseStack.translate(t.x(), t.y(), t.z());
            poseStack.scale(0.25f, 0.25f, 0.25f);
            poseStack.mulPose(Axis.YP.rotationDegrees(ticks + (i * angleBetweenEach)));
            poseStack.translate(distanceVec.x(),
                    distanceVec.y() + ((i % 2 == 0 ? -i : i) * Mth.sin(ticks / 60f) * 0.0625f),
                    distanceVec.z());
            poseStack.mulPose((i % 2 == 0 ? Axis.ZP : Axis.XP).rotationDegrees(ticks));
            ItemEntityRenderer.submitMultipleFromCount(poseStack, collector, state.lightCoords, state.orbitItems.get(i), random);
            poseStack.popPose();
        }
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }

    @Override
    public boolean shouldRenderOffScreen() { return true; }

    @Override
    public int getViewDistance() { return 256; }

    @Override
    public AABB getRenderBoundingBox(ScribesTile blockEntity) { return AABB.INFINITE; }
}
