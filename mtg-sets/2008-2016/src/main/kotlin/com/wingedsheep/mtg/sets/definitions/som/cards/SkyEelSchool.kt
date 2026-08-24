package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sky-Eel School — Scars of Mirrodin #44
 * {3}{U}{U} · Creature — Fish · 3 / 3
 *
 * Flying
 * When this creature enters, draw a card, then discard a card.
 *
 * "Draw a card, then discard a card" is looting, and `Patterns.Hand.loot()` is the composition:
 * the draw first, then the Gather → Select → Move spine that makes the discard a real choice from
 * the post-draw hand rather than a pre-selected card.
 */
val SkyEelSchool = card("Sky-Eel School") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Fish"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "When this creature enters, draw a card, then discard a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.loot()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Daniel Ljunggren"
        flavorText = "They swim on tides few can see, away from a threat few yet understand."
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5cfc4db7-13b5-4c88-91f2-581c9792f1ff.jpg?1783941737"
    }
}
