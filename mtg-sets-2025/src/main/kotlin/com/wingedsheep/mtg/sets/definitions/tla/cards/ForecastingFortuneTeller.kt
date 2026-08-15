package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Forecasting Fortune Teller
 * {1}{U}
 * Creature — Human Advisor Ally
 * 1/3
 *
 * When this creature enters, create a Clue token. (It's an artifact with
 * "{2}, Sacrifice this token: Draw a card.")
 */
val ForecastingFortuneTeller = card("Forecasting Fortune Teller") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Advisor Ally"
    power = 1
    toughness = 3
    oracleText = "When this creature enters, create a Clue token. " +
        "(It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateClue()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "51"
        artist = "Hisashi Momose"
        flavorText = "Many believe that clouds tell the future, but they do not shape it."
        imageUri = "https://cards.scryfall.io/normal/front/0/2/023e7ad4-6af0-4f29-a716-27a313974227.jpg?1764120239"
    }
}
