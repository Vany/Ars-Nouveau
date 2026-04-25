package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.client.renderer.ANDataTickets;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// GeckoLib 5: color injected via ANDataTickets.BOOKWYRM_COLOR
// TODO: Port 0.6f scale from old render() override to scaleModelForRender(RenderPassInfo, float, float)
public class BookwyrmRenderer extends GeoEntityRenderer<EntityBookwyrm, ArsEntityRenderState> {

    public BookwyrmRenderer(EntityRendererProvider.Context manager) {
        super(manager, new BookwyrmModel<>());
    }

    @Override
    public ArsEntityRenderState createRenderState(EntityBookwyrm animatable, Void context) {
        return new ArsEntityRenderState();
    }

    @Override
    public void addRenderData(EntityBookwyrm animatable, @Nullable Void relatedObject, ArsEntityRenderState renderState, float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);
        renderState.addGeckolibData(ANDataTickets.BOOKWYRM_COLOR, animatable.getColor());
    }

    // TODO: Port 0.6f scale from old render() override to scaleModelForRender(RenderPassInfo, float, float)
    @Override
    public void scaleModelForRender(software.bernie.geckolib.renderer.base.RenderPassInfo<ArsEntityRenderState> renderPassInfo, float width, float height) {
        renderPassInfo.poseStack().scale(0.6f, 0.6f, 0.6f);
        super.scaleModelForRender(renderPassInfo, width, height);
    }
}
