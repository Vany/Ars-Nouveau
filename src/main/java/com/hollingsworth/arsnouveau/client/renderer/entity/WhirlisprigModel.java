package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: getModelResource/getTextureResource now take GeoRenderState
// setCustomAnimations, getAnimationProcessor().getBone(), GeoBone.setRotX/Y/Z removed
// TODO: Port head rotation (headPitch, netHeadYaw) to captureDefaultRenderState
public class WhirlisprigModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("whirlisprig");
    }

    // whirlisprig.png never existed; seasonal textures exist but Serene Seasons has no 1.21.11 version yet
    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("textures/entity/whirlisprig_summer.png");
    }

    @Override
    public Identifier getAnimationResource(T whirlisprig) {
        return ArsNouveau.prefix("whirlisprig_animations");
    }
}
