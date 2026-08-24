package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Darkslick Drake
 * {2}{U}{U}
 * Creature — Phyrexian Drake
 * 2/4
 *
 * Flying
 * When this creature dies, draw a card.
 */
val DarkslickDrake = card("Darkslick Drake") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Phyrexian Drake"
    power = 2
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature dies, draw a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(1)
        description = "When this creature dies, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "30"
        artist = "Chippy"
        flavorText = "At the edge of the Mephidross, Phyrexia's influence seeps into life and land."
        imageUri = "https://cards.scryfall.io/normal/front/2/3/234f4131-1e7f-4220-b46c-bb4a6713876e.jpg?1783941740"
    }
}
