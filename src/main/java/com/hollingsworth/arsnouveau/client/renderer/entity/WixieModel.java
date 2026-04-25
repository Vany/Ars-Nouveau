package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: getTextureResource reads WIXIE_COLOR from render state (set by renderer in addRenderData)
public class WixieModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("wixie");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        String color = renderState.getGeckolibData(com.hollingsworth.arsnouveau.client.renderer.ANDataTickets.WIXIE_COLOR);
        if (color == null || color.isEmpty()) color = "blue";
        return ArsNouveau.prefix("textures/entity/wixie_" + color + ".png");
    }

    @Override
    public Identifier getAnimationResource(T entityWixie) {
        return ArsNouveau.prefix("wixie_animations");
    }
}
