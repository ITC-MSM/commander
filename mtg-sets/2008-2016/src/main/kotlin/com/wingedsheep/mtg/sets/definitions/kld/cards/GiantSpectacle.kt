package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Giant Spectacle
 * {1}{R}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +2/+1 and has menace.
 *
 * Two statics over the Aura's default [com.wingedsheep.sdk.scripting.filters.unified.GroupFilter]
 * scope (the attached creature) — the printed "and" is two separate continuous effects, not one
 * combined ability.
 */
val GiantSpectacle = card("Giant Spectacle") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+1 and has menace."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.MENACE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "116"
        artist = "Johann Bodin"
        flavorText = "The giant-decorating contest at the migration festival is a tradition that may have started as a dare."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c425337-6f1f-494e-a7ae-7d533d7a0b4e.jpg?1783937194"
    }
}
