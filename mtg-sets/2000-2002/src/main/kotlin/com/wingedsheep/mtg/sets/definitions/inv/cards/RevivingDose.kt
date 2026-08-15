package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Reviving Dose
 * {2}{W}
 * Instant
 * You gain 3 life.
 * Draw a card.
 */
val RevivingDose = card("Reviving Dose") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "You gain 3 life.\nDraw a card."

    spell {
        effect = Effects.GainLife(3).then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31"
        artist = "D. Alexander Gregory"
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d44dd88-ad20-4d89-8831-d2dfa6873428.jpg?1562923533"
    }
}
