package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Volcanic Strength
 * {1}{R}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +2/+2 and has mountainwalk. (It can't be blocked as long as defending player controls a Mountain.)
 *
 * Cave Sense's bigger sibling — the same two-static Aura shape ([ModifyStats] plus a
 * [GrantKeyword]), one size up. The printed "and" joins two separate static abilities rather than
 * one combined type; neither carries a filter, so the Aura's auto-targeting applies both to the
 * enchanted creature. `auraTarget = Targets.Creature` is the "Enchant creature" restriction.
 * Granted landwalk is engine-live: `BlockEvasionRules.LandwalkRule` maps `MOUNTAINWALK` to the
 * Mountain subtype off projected state.
 */
val VolcanicStrength = card("Volcanic Strength") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+2 and has mountainwalk. (It can't be blocked as long as defending player controls a Mountain.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.MOUNTAINWALK)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "Izzy"
        flavorText = "His blood boiled over, and he erupted with fists of stone."
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bda0bffa-c58c-4630-8899-a1b332a7b8dc.jpg?1783941802"
    }
}
