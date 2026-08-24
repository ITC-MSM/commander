package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nightveil Sprite
 * {1}{U}
 * Creature — Faerie Rogue
 * 1/2
 * Flying
 * Whenever this creature attacks, surveil 1. (Look at the top card of your library. You may put it into your graveyard.)
 */
val NightveilSprite = card("Nightveil Sprite") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Rogue"
    oracleText = "Flying\n" +
        "Whenever this creature attacks, surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"
    power = 1
    toughness = 2

    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Patterns.Library.surveil(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "48"
        artist = "Uriah Voth"
        flavorText = "\"We're on the fortieth floor, with one window, no balcony. No one could possibly get in.\"\n—Minosz, Orzhov chief of security"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/534d1166-4e01-4ec8-b4d9-e76861ec51b9.jpg?1783934187"
    }
}
