package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock

/**
 * Nightshade Stinger
 * {B}
 * Creature — Faerie Rogue
 * 1/1
 * Flying
 * This creature can't block.
 */
val NightshadeStinger = card("Nightshade Stinger") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Faerie Rogue"
    power = 1
    toughness = 1
    oracleText = "Flying\nThis creature can't block."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CantBlock()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "132"
        artist = "Mark Poole"
        flavorText = "\"Most faeries are harmless pranksters. Every now and again, though, you get one that crosses over from mischievous to malicious.\"\n—Gaddock Teeg"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa341824-bf3a-49ce-a8a0-ee53f537d626.jpg?1783942886"
    }
}
