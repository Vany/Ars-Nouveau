package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.ANDataTickets;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: texture/model injected via ANDataTickets.STARBUNCLE_TEXTURE/MODEL in addRenderData
// TODO: Port basket bone visibility (entity.isTamed()) to addAdditionalStateData pattern.
public class StarbuncleModel extends GeoModel<Starbuncle> {

    public static final Identifier ANIMATION = ArsNouveau.prefix("starbuncle_animations");
    private static final Identifier DEFAULT_MODEL = ArsNouveau.prefix("starbuncle");
    private static final Identifier DEFAULT_TEXTURE = ArsNouveau.prefix("textures/entity/starbuncle_orange.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        Identifier model = renderState.getGeckolibData(ANDataTickets.STARBUNCLE_MODEL);
        return model != null ? model : DEFAULT_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        Identifier texture = renderState.getGeckolibData(ANDataTickets.STARBUNCLE_TEXTURE);
        return texture != null ? texture : DEFAULT_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(Starbuncle carbuncle) {
        return ANIMATION;
    }
}
