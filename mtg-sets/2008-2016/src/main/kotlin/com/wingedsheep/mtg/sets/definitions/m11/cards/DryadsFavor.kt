package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Dryad's Favor
 * {G}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has forestwalk. (It can't be blocked as long as defending player controls a Forest.)
 *
 * The green single-keyword member of the landwalk-Aura family — Cave Sense without the pump.
 * `auraTarget = Targets.Creature` is the printed "Enchant creature" restriction; the static
 * [GrantKeyword] has no filter, so the usual Aura auto-targeting points it at the enchanted
 * creature. Granted landwalk is engine-live: `BlockEvasionRules.LandwalkRule` reads the keyword out
 * of projected state and maps `FORESTWALK` to the Forest subtype.
 */
val DryadsFavor = card("Dryad's Favor") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has forestwalk. (It can't be blocked as long as defending player controls a Forest.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.FORESTWALK)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Jesper Ejsing"
        flavorText = "She grants knowledge of the ways to traverse the forest's invisible connections."
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c259509e-9f95-4566-b78a-ba34107539f7.jpg?1783941799"
    }
}
