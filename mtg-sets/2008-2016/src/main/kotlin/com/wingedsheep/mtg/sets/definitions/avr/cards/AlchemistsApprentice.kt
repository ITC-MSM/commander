package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Alchemist's Apprentice
 * {1}{U}
 * Creature — Human Wizard
 * 1 / 1
 *
 * Sacrifice this creature: Draw a card.
 */
val AlchemistsApprentice = card("Alchemist's Apprentice") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "Sacrifice this creature: Draw a card."

    activatedAbility {
        cost = Costs.SacrificeSelf
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "42"
        artist = "David Palumbo"
        flavorText = "Side effects may include foul odors, scalding steam, and spontaneous nonexistence."
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31abba67-1241-4fb3-88b5-4c4668ec5f25.jpg?1783940724"
    }
}
