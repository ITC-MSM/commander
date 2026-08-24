package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Lightning Talons
 * {2}{R}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +3/+0 and has first strike. (It deals combat damage before creatures without first strike.)
 *
 * "Enchant creature" is the aura's attachment restriction *and* its cast-time target, so it is
 * `auraTarget = `[Targets.Creature] rather than a `target()` handle. The printed "and" joins two
 * independent grants in different layers — a P/T modification (layer 7c) and an ability grant
 * (layer 6) — so it is two [ModifyStats] / [GrantKeyword] static abilities over the implicit
 * enchanted permanent, not one fused effect.
 */
val LightningTalons = card("Lightning Talons") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +3/+0 and has first strike. (It deals combat damage before creatures without first strike.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(3, 0)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "107"
        artist = "Pete Venters"
        flavorText = "\"This is going to hurt a lot, but on the bright side, you'll be dead soon.\"\n—Sedris, the Traitor King"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8fc1ad4c-b394-48b9-9a5a-ec9f42bf6a00.jpg"
    }
}
