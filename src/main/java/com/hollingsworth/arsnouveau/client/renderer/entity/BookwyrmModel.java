package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.ANDataTickets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: color injected via ANDataTickets.BOOKWYRM_COLOR in addRenderData
// TODO: Port head rotation (headPitch, netHeadYaw) to captureDefaultRenderState
public class BookwyrmModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {

    public static final Identifier NORMAL_MODEL = ArsNouveau.prefix("book_wyrm");
    public static final Identifier ANIMATIONS = ArsNouveau.prefix("book_wyrm_animation");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return NORMAL_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        String color = renderState.getGeckolibData(ANDataTickets.BOOKWYRM_COLOR);
        if (color == null || color.isEmpty()) color = "blue";
        return ArsNouveau.prefix("textures/entity/book_wyrm_" + color + ".png");
    }

    @Override
    public Identifier getAnimationResource(T wyrm) {
        return ANIMATIONS;
    }
}
