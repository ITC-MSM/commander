package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Exclude
 * {2}{U}
 * Instant
 * Counter target creature spell.
 * Draw a card.
 */
val Exclude = card("Exclude") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target creature spell.\nDraw a card."

    spell {
        target = Targets.CreatureSpell
        effect = Effects.CounterSpell() then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Mark Romanoski"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/aeb359c8-209c-455f-84b2-970e5678a9fa.jpg?1562930137"
    }
}
