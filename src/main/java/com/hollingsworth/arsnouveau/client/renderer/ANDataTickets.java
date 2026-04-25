package com.hollingsworth.arsnouveau.client.renderer;

import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;

/**
 * Singleton DataTickets for Ars Nouveau GeckoLib render state data.
 * Must be singletons because ArsEntityRenderState uses Reference2ObjectOpenHashMap (identity keys).
 */
public class ANDataTickets {
    public static final DataTicket<String> DRYGMY_COLOR = DataTicket.create("ars_nouveau_drygmy_color", String.class);
    public static final DataTicket<String> WIXIE_COLOR = DataTicket.create("ars_nouveau_wixie_color", String.class);
    public static final DataTicket<String> BOOKWYRM_COLOR = DataTicket.create("ars_nouveau_bookwyrm_color", String.class);
    // Starbuncle texture/model are Identifier-typed because named starbuncles (Zieg, Xacris, etc.) have unique assets
    public static final DataTicket<Identifier> STARBUNCLE_TEXTURE = DataTicket.create("ars_nouveau_starbuncle_texture", Identifier.class);
    public static final DataTicket<Identifier> STARBUNCLE_MODEL = DataTicket.create("ars_nouveau_starbuncle_model", Identifier.class);
    public static final DataTicket<String> DYE_COLOR = DataTicket.create("ars_nouveau_dye_color", String.class);
    // Projectile trajectory rotation, stored in extractRenderState for use in adjustRenderPose
    public static final DataTicket<Float> PROJ_Y_ROT = DataTicket.create("ars_nouveau_proj_y_rot", Float.class);
    public static final DataTicket<Float> PROJ_X_ROT = DataTicket.create("ars_nouveau_proj_x_rot", Float.class);
    // Starbuncle display name — used for special shader lookup (e.g. "Splonk", "Bailey")
    public static final DataTicket<String> ENTITY_NAME = DataTicket.create("ars_nouveau_entity_name", String.class);
}
