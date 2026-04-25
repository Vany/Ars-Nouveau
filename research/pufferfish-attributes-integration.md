# Pufferfish Attributes + Ars Nouveau Integration

## Installed mods (VanyLLa3d instance)
- `puffish_attributes-0.8.2-1.21.11-neoforge.jar` — attribute system
- `puffish_skills-0.17.3-1.21.11-neoforge.jar` — skill tree UI
- `default_skill_trees-1.1-1.21.9.jar` — default combat/mining skill trees (datapack)

## How puffish_skills grants attributes
Skill nodes in `data/puffish_skills/puffish_skills/categories/<name>/definitions.json`:
```json
"node_id": {
  "title": "+0.5 Spell Damage",
  "rewards": [{
    "type": "puffish_skills:attribute",
    "data": {
      "attribute": "<namespace>:<path>",
      "value": 0.5,
      "operation": "addition"
    }
  }]
}
```
Operations: `addition`, `multiply_base`, `multiply_total`.

## Ars Nouveau attributes (PerkAttributes.java)
All registered under namespace `ars_nouveau`, all added to PLAYER entity type.
Note: registry path includes the namespace prefix (e.g. path = `ars_nouveau.perk.spell_damage`)
so the full ID looks redundant: `ars_nouveau:ars_nouveau.perk.spell_damage`.

| Full attribute ID | Effect | Default | Range | Recommended operation |
|---|---|---|---|---|
| `ars_nouveau:ars_nouveau.perk.spell_damage` | Flat bonus added to every spell hit damage | 0 | 0–10000 | `addition` |
| `ars_nouveau:ars_nouveau.perk.max_mana` | Flat bonus to max mana pool | 0 | 0–10000 | `addition` |
| `ars_nouveau:ars_nouveau.perk.mana_regen` | Flat bonus to mana regen per tick | 0 | 0–2000 | `addition` |
| `ars_nouveau:ars_nouveau.perk.warding` | Flat subtraction from `IS_MAGIC` damage; 1.0 = −1 dmg | 0 | 0–1024 | `addition` |
| `ars_nouveau:ars_nouveau.perk.feather` | Fall damage reduction: `dmg *= (1 - feather)`; 1.0 = immune | 0 | **0–1** | `addition` (small steps, e.g. 0.1) |
| `ars_nouveau:ars_nouveau.perk.saturation` | Multiplier on food saturation gained | 1.0 | 0–10000 | `multiply_total` |
| `ars_nouveau:ars_nouveau.perk.weight` | Gravity multiplier: `g *= weight`; >1 heavier, <1 floaty | 1.0 | 0–100 | `multiply_total` |
| `ars_nouveau:ars_nouveau.perk.wixie` | Wixie familiar loot bonus | 1.0 | 0–1024 | `addition` |
| `ars_nouveau:ars_nouveau.perk.drygmy` | Drygmy familiar drop bonus | 0 | 0–1024 | `addition` |
| `ars_nouveau:ars_nouveau.perk.spell_length` | Extra glyph slots in spellbook (integer) | 0 | 0–1000 | `addition` |

## Where each attribute is consumed (source references)
- `spell_damage` — `IDamageEffect.java`: added to base damage before final spell hit calc
- `max_mana` — `ManaUtil.java`: injected as transient modifier on player's max mana attribute
- `mana_regen` — `ManaUtil.java`: injected as transient modifier on mana regen attribute
- `warding` — `EventHandler.java` LivingHurt: subtracted from amount when `source.is(IS_MAGIC)`
- `feather` — `EventHandler.java` LivingHurt: `amount -= amount * feather` when `source.is(FALL)`
- `saturation` — `EventHandler.java`: `saturationLevel *= perkValue(player, WHIRLIESPRIG)`
- `weight` — `LivingEntityMixin.java`: `gravity *= weight.getValue()`
- `wixie` — `EventHandler.java`: looting/drop bonus when Wixie familiar active
- `drygmy` — `EventHandler.java`: drop bonus for Drygmy familiar
- `spell_length` — `GuiSpellBook.getExtraGlyphSlots()`: `(int) attributeValue` added to bonus slots; base is 10, so total = `10 + spell_length + bonusSlots + configBonus`

## Recommended mage skill tree nodes
Good candidates for a dedicated Ars Nouveau magic skill category:

```
spell_damage    +0.5 per node   (offensive mage scaling)
max_mana        +50 per node    (larger mana pool)
mana_regen      +1.0 per node   (faster recovery)
warding         +0.5 per node   (magic damage tank)
feather         +0.1 per node   (fall utility, max 10 nodes = immune)
saturation      multiply_total 0.05 per node  (food efficiency)
spell_length    +1 per node     (longer spells, integer — use addition)
```

Skip for general trees: `wixie` / `drygmy` — only meaningful with specific familiars active.
`weight` — interesting for a niche "gravity" node but unusual for a standard tree.

## Test command
```
/attribute @p ars_nouveau:ars_nouveau.perk.spell_length base set 5
```
