# TODO: Ars Nouveau 1.21.1 → 1.21.11 Migration


## bugs

- table rendering it rnders out of the hit box
- all mana jars in inventory rendered empty even if not.
- mana jar have no correct tooltip, we need to distinguish full and empty.
- most of the strings have no visible english translation
- table  show flying items, but not orbit required ones

Work through tasks in order — each group should compile cleanly before moving on.
See `MEMO.md` for API change details. See `research/` for verified sources.

BUGS:

FIXED:
- whirlisprig purple-black texture — WhirlisprigModel returned nonexistent `whirlisprig.png`; fallback to `whirlisprig_summer.png` (Serene Seasons unavailable in 1.21.11)
- WhirlisprigFlower.getRenderShape() returned MODEL instead of INVISIBLE (inconsistent with all other GeckoLib blocks)
- scribes table rendered twice — submit() guard skips non-HEAD parts (FOOT+OTHER each have own tile entity)
- alteration table rendered 3× — same fix (3-part block: FOOT+HEAD+OTHER)
- imbuement chamber doesn't render content — ported to ItemClusterRenderState + extractRenderState pattern
- scribes table item/ingredient rendering — ported pressed-item + orbiting ingredients to same pattern

---

## 1. Build Config

- [x] **Add Curios to build.gradle** — already present: `implementation "top.theillusivec4.curios:curios-neoforge:${curios_version}+1.21.11"`
- [x] **Add JEI to build.gradle** — already present: `compileOnly "mezz.jei:jei-1.21.11-neoforge-api:${jei_version}"`

- [ ] **Patchouli** — no 1.21.11 version exists yet. Keep commented out.
  Files using Patchouli: `PatchouliHandler.java`, `client/patchouli/*`.
  Decision needed: stub them out or guard with `#if PATCHOULI`.

- [ ] **Caelus** — no 1.21.11 version found. Keep commented out.
  File: `CaelusHandler.java`. Guard with try/catch or remove temporarily.

---

## 2. Item API Fixes

### 2a. EnchantersSword — constructor body
- [x] Already fixed: `super(properties)`, `iItemTier.attackDamageBonus()`, `DataComponents.TOOL` component used

### 2b. appendHoverText — ~44 files
- [x] All item/block files verified using new signature `(ItemStack, Item.TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)`
- [x] `AllayBehavior.java` — fixed call-site: now passes `TooltipDisplay.DEFAULT, tooltips::add`

### 2c. BannerPatternItem → BannerItem
- [x] Not found in codebase — already removed/replaced

### 2d. DeferredSpawnEggItem → SpawnEggItem
- [x] Not found in codebase — already replaced

---

## 3. Block API Fixes

### 3a. ItemInteractionResult → InteractionResult
- [x] Not found in codebase — already replaced

### 3b. DirectionProperty → EnumProperty<Direction>
- [x] Not found in codebase — already replaced

### 3c. ItemNameBlockItem → BlockItem
- [x] Not found in codebase — already replaced

### 3d. BlockEntityWithoutLevelRenderer / IClientItemExtensions
- [x] No active usage found (only a TODO comment in ClientEvents.java line 53)

---

## 4. Entity API Fixes

### 4a. FlyingMob → PathfinderMob
- [x] EntityBookwyrm already extends `PathfinderMob`

### 4b. DimensionTransition → TeleportTransition
- [x] PortalTile.java already uses `TeleportTransition` (verified: import + usage correct)

### 4c. RemovalReason → Entity.RemovalReason
- [x] Fixed 2025-03-18: replaced bare `RemovalReason` with `Entity.RemovalReason` in 24 files
  (EntityFlyingItem, EntitySpellArrow, EntityFollowProjectile, EntityDummy, EntityRitualProjectile,
  BubbleEntity, Starbuncle, FamiliarEntity, Whirlisprig, SummonHorse, ScryerCamera, GiftStarbuncle,
  AnimBlockSummon, EntityWallSpell, AmethystGolem, EntityWixie, Alakarkinos, EntityEvokerFangs,
  SummonWolf, LightningEntity, EntityDrygmy, EntityProjectileSpell, EntityLingeringSpell, Nook)

### 4d. WeightedEntry — verify package
- [x] Not found in codebase — no usage present

---

## 5. BlockEntity API Fixes

### 5a. DataComponentGetter in MobJarTile
- [x] Already uses `DataComponentGetter` correctly in MobJarTile, RepositoryCatalogTile, PotionJarTile, PlanariumTile, AbstractSourceMachine

### 5b. INBTSerializable — remove interface
- [x] Not found in codebase — already removed

---

## 6. GeckoLib 4 → 5 Migration (~70 files)

See `research/geckolib5-imports.md` for full import table.

- [x] All 20 files with `geckolib.animation.*` wildcard also have explicit imports for:
  - `software.bernie.geckolib.animation.state.AnimationTest`
  - `software.bernie.geckolib.animation.object.PlayState`
  - Other needed subpackage classes
- [x] `AnimationState` → `AnimationTest` rename already done in all files
  (WildenGuardian, WildenStalker, WildenHunter, WhirlisprigTile, EntityWixie, Whirlisprig, etc.)
- [x] `AnimationController<>` diamond / raw type compile errors — fixed all ~35 files
  (GeckoLib 5 constructor has generic predicate parameter that breaks diamond inference)

---

## 7. Nuggets Library

Files inlined into src keeping package `com.hollingsworth.nuggets.*`.
Issue: `BaseScreen` has a mixin import that may fail compilation.

- [x] `BaseScreen.java` imports `ScreenAccessor` from `com.hollingsworth.arsnouveau.common.mixin` — mixin exists and is correct
- [x] Nuggets dependency removed from build.gradle (classes are inlined)

---

## 8. Verify & Build

- [x] Static analysis pass 1: ~100 compile errors identified and fixed (2026-03-18 session)
- [x] Static analysis pass 2: GeckoLib AnimationController, causeFallDamage, hurt→hurtServer, GameRules, RemovalReason, etc. (~35 more files)
- [x] Static analysis pass 3: Capabilities API, GUI rendering, recipe matches, StructureTemplate, entity rendering in GUI (2026-03-23)
- [x] EffectCrush: wrapped ItemStack in SingleRecipeInput for matches() calls
- [x] Run `./gradlew compileJava` — 0 errors (2026-03-28)
- [x] Run `./gradlew build` — BUILD SUCCESSFUL (2026-03-28)
- [x] Runtime check pass 1: mod loads in-game, no crashes. Fixed:
  - 425 items missing `assets/ars_nouveau/items/*.json` (MC 1.21.11 new item model system)
  - `particle_block` missing blockstate file (waterlogged=true/false variants)
  - `planarium_projector` missing blockstate + item model
  - `redstone_relay` block model missing `"all"` texture slot
  - `IndexScreen` (doc screen) blur crash — fixed in BaseScreen.drawScreenAfterScale (no longer calls renderBackground)
- [x] Doc screen null crash: `PedestalRecipeEntry.reagentStack` null check + `EnchantmentEntry` null guard
- [x] Archwood chest: 3D item rendering via `minecraft:special` + `CHEST_MAPPER` texture paths
- [x] `RenderFlyingItem`: ported to ItemModelResolver + ItemStackRenderState pipeline
- [x] 45 GeoItem `items/*.json` → `geckolib:geckolib` special renderer (wand, spell books, turrets, relays, etc.)
- [x] 0 ars_nouveau startup warnings (only vanilla MC `template_spawn_egg` remains)
- [x] Runtime check pass 2 (2026-03-28): GeckoLib render state map mismatch fixed
  - EntityDrygmy NPE on `ANIMATABLE_MANAGER` — `addGeckolibData`/`getDataMap` split in mixin; fixed in all 3 render state classes
  - Block entity same NPE — same fix for `ArsBlockEntityRenderState`
  - Drygmy missing texture — `DrygmyModel` now reads color from render state, returns `drygmy_brown.png` etc.
  - Armor `ClassCastException` (`AvatarRenderState → ArsHumanoidRenderState`) — moved dye color injection to `DyeableGeoModel.addAdditionalStateData`
- [x] i18n audit — all `Component.literal(...)` user strings replaced with `Component.translatable(...)`; 12 new keys added to `en_us.json`
- [x] Spellbook edit fields (SearchBar + EnterTextField) invisible text — root cause: identity matrix context drops text in MC 1.21.11 deferred pipeline. Fix: wrap `super.renderWidget` in `pushMatrix/translate(getX,getY)/popMatrix`. Confirmed working.
- [x] EnterTextField suggestion overlap — `EditBox` shows suggestion at cursor-end position even when value non-empty. Fix: null suggestion before super when `!value.isEmpty()`, restore after.
- [x] EnchantingApparatus + ArcaneCore model flat/horizontal — GeckoLib 5 `tryRotateByBlockstate` misrotates 6-way FACING blocks placed UP. Fix: override `adjustRenderPose` (empty body) in EnchantingApparatusRenderer, ArcaneCoreRenderer, ImbuementRenderer.
- [x] Corrupted jar crash (ZipException bad LOC header) — failed `cp` write; rebuild and reinstall fixes it.
- [ ] LecternRenderer + RedstoneRelayRenderer: verify in-game (horizontal FACING — tryRotateByBlockstate may need suppressing)
- [ ] Strip debug LOGGER calls from EnchantingApparatusBlock before release
- [ ] Test in-game: basic spell casting, familiars, rituals, doc screen, GeoItem rendering in inventory, armor dye colors, apparatus crafting

---

## Deferred / Low Priority

- [ ] **EMI** — no 1.21.11 version found. Keep commented out.
- [ ] **TerraBlender** — no 1.21.11 version found. Keep commented out.
- [ ] **LambDynamicLights** — no 1.21.11 version found. Keep commented out.
- [ ] **Patchouli integration** — revisit when a 1.21.11 version is published.
