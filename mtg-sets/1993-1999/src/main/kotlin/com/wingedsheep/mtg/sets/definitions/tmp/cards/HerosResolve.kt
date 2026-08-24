package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Hero's Resolve
 * {1}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +1/+5.
 */
val HerosResolve = card("Hero's Resolve") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+5."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 5)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Pete Venters"
        flavorText = "\"Destiny, chance, fate, fortune—they're all just ways of claiming your successes without claiming your failures.\"\n" +
            "—Gerrard of the *Weatherlight*"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4cdcc7c-0d01-4aa2-8934-079dfc00eef2.jpg"
    }
}
