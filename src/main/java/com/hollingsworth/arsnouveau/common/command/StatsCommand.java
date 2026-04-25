package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class StatsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-stats")
                .requires(Commands.hasPermission(Commands.LEVEL_ALL))
                .executes(context -> showStats(context.getSource())));
    }

    private static int showStats(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        var mana = CapabilityRegistry.getMana(player);
        if (mana == null) return 1;

        ManaUtil.Mana maxMana = ManaUtil.calcMaxMana(player);
        double regenPerSec = ManaUtil.getManaRegen(player);
        double current = mana.getCurrentMana();

        double warding = PerkUtil.valueOrZero(player, PerkAttributes.WARDING);
        double spellDamage = PerkUtil.valueOrZero(player, PerkAttributes.SPELL_DAMAGE_BONUS);
        double spellLength = PerkUtil.valueOrZero(player, PerkAttributes.SPELL_LENGTH);
        double feather = PerkUtil.valueOrZero(player, PerkAttributes.FEATHER);

        player.sendSystemMessage(Component.translatable("ars_nouveau.command.stats.header"));
        player.sendSystemMessage(Component.translatable("ars_nouveau.command.stats.mana",
                (int) current, maxMana.getRealMax(), maxMana.Max()));
        player.sendSystemMessage(Component.translatable("ars_nouveau.command.stats.mana_regen",
                String.format("%.1f", regenPerSec)));
        player.sendSystemMessage(Component.translatable("ars_nouveau.command.stats.book_tier",
                mana.getBookTier(), mana.getGlyphBonus()));
        player.sendSystemMessage(Component.translatable("ars_nouveau.command.stats.warding",
                (int) warding));
        player.sendSystemMessage(Component.translatable("ars_nouveau.command.stats.spell_damage",
                (int) spellDamage));
        player.sendSystemMessage(Component.translatable("ars_nouveau.command.stats.spell_length",
                (int) spellLength));
        if (feather > 0) {
            player.sendSystemMessage(Component.translatable("ars_nouveau.command.stats.feather",
                    (int) (feather * 100)));
        }

        return 1;
    }
}
