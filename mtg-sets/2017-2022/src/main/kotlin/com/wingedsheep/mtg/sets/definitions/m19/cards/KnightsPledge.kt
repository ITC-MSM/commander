package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Knight's Pledge
 * {1}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +2/+2.
 */
val KnightsPledge = card("Knight's Pledge") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+2."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Magali Villeneuve"
        flavorText = "\"As long as my faith persists, so shall I.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/3/734ff6ac-000d-4fc6-b97b-07b9b21f745c.jpg"
    }
}
