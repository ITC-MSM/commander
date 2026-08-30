package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mad Prophet
 * {3}{R}
 * Creature — Human Shaman
 * 2 / 2
 *
 * Haste
 * {T}, Discard a card: Draw a card.
 */
val MadProphet = card("Mad Prophet") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Shaman"
    power = 2
    toughness = 2
    oracleText = "Haste\n" +
        "{T}, Discard a card: Draw a card."

    keywords(Keyword.HASTE)

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.DiscardCard)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Wayne Reynolds"
        flavorText = "\"There's no heron in the moon! It's a shrew, a five-legged shrew, with a voice like whispering thunder!\""
        imageUri = "https://cards.scryfall.io/normal/front/1/7/172383d9-9135-4daa-a647-9d76435d3158.jpg?1783940683"
    }
}
