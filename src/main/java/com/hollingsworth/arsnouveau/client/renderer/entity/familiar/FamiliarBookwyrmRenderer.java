package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.client.renderer.ANDataTickets;
import com.hollingsworth.arsnouveau.client.renderer.entity.ArsEntityRenderState;
import com.hollingsworth.arsnouveau.client.renderer.entity.BookwyrmModel;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

// GeckoLib 5: withScale(0.5f) replaces the old 0.5x PoseStack scale in render()
public class FamiliarBookwyrmRenderer extends GenericFamiliarRenderer<FamiliarBookwyrm> {
    public FamiliarBookwyrmRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BookwyrmModel<>());
        withScale(0.5f);
    }

    @Override
    public void addRenderData(FamiliarBookwyrm entity, Void context, ArsEntityRenderState state, float partialTick) {
        super.addRenderData(entity, context, state, partialTick);
        state.addGeckolibData(ANDataTickets.BOOKWYRM_COLOR, entity.getColor());
    }
}
