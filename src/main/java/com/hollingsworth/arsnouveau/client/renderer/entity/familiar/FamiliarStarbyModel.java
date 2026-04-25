package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.ANDataTickets;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: texture/model injected via ANDataTickets.STARBUNCLE_TEXTURE/MODEL in addRenderData
// TODO: Port head rotation to GeckoLib 5 addPerBoneRender pattern
public class FamiliarStarbyModel<T extends FamiliarStarbuncle> extends GeoModel<T> {

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
    public Identifier getAnimationResource(FamiliarStarbuncle carbuncle) {
        return ArsNouveau.prefix("starbuncle_animations");
    }

}
