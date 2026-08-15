package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dismiss
 * {2}{U}{U}
 * Instant
 * Counter target spell.
 * Draw a card.
 */
val Dismiss = card("Dismiss") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell.\nDraw a card."

    spell {
        target = Targets.Spell
        effect = Effects.CounterSpell() then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "58"
        artist = "Donato Giancola"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1e55d6be-7682-4786-9872-e847afd710b0.jpg?1562052798"
    }
}
