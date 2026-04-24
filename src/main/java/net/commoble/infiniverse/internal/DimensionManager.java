package net.commoble.infiniverse.internal;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.common.mixin.MinecraftServerAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Dynamic dimension creation — replaces the infiniverse library which has no 1.21.11 version.
 * Uses NeoForge's forgeGetWorldMap() to register new ServerLevels at runtime.
 * Levels are not persisted in level.dat (not in LEVEL_STEM registry) but their
 * chunk data is saved to disk via the server's storageSource like any vanilla dimension.
 */
public class DimensionManager {
    public static final DimensionManager INSTANCE = new DimensionManager();
    private static final Logger LOGGER = LoggerFactory.getLogger(DimensionManager.class);

    public ServerLevel getOrCreateLevel(MinecraftServer server, ResourceKey<Level> key, Supplier<LevelStem> dimensionFactory) {
        Map<ResourceKey<Level>, ServerLevel> levels = server.forgeGetWorldMap();

        ServerLevel existing = levels.get(key);
        if (existing != null) return existing;

        LOGGER.info("[Planarium] Creating dynamic dimension: {}", key.identifier());

        ServerLevel overworld = server.overworld();
        ServerLevelData overworldData = overworld.getServer().getWorldData().overworldData();
        DerivedLevelData derivedData = new DerivedLevelData(server.getWorldData(), overworldData);
        LevelStem levelStem = dimensionFactory.get();

        ServerLevel newLevel = new ServerLevel(
                server,
                ((MinecraftServerAccessor) server).getExecutor(),
                ((MinecraftServerAccessor) server).getStorageSource(),
                derivedData,
                key,
                levelStem,
                false,
                overworld.getSeed(),
                ImmutableList.of(),
                false,
                overworld.getRandomSequences()
        );

        levels.put(key, newLevel);
        server.markWorldsDirty();
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(newLevel));

        LOGGER.info("[Planarium] Dynamic dimension created: {}", key.identifier());
        return newLevel;
    }
}
