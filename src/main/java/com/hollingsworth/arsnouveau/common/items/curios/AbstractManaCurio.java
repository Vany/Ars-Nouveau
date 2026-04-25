package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CurioAttributeModifiers;

public abstract class AbstractManaCurio extends ArsNouveauCurio {
    public AbstractManaCurio() {
        super();
    }

    public int getMaxManaBoost(ItemStack i) {
        return 0;
    }

    public int getManaRegenBonus(ItemStack i) {
        return 0;
    }

    // Curios 14: attribute modifiers are read via getDefaultCurioAttributeModifiers, not the old
    // getAttributeModifiers(SlotContext, Identifier, ItemStack) which is no longer called by the tick loop.
    // Modifier IDs are derived from the item's registry path to avoid conflicts when multiple curios
    // with the same attribute are equipped simultaneously.
    @Override
    public CurioAttributeModifiers getDefaultCurioAttributeModifiers(ItemStack stack) {
        Identifier key = BuiltInRegistries.ITEM.getKey(this);
        String path = key != null ? key.getPath() : getClass().getSimpleName().toLowerCase();
        var builder = CurioAttributeModifiers.builder();
        int maxMana = getMaxManaBoost(stack);
        if (maxMana != 0)
            builder.addModifier(PerkAttributes.MAX_MANA,
                new AttributeModifier(Identifier.fromNamespaceAndPath("ars_nouveau", path + "_max_mana"),
                    maxMana, AttributeModifier.Operation.ADD_VALUE));
        int manaRegen = getManaRegenBonus(stack);
        if (manaRegen != 0)
            builder.addModifier(PerkAttributes.MANA_REGEN_BONUS,
                new AttributeModifier(Identifier.fromNamespaceAndPath("ars_nouveau", path + "_mana_regen"),
                    manaRegen, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }
}
