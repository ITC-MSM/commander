package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kavu Climber
 * {3}{G}{G}
 * Creature — Kavu
 * 3/3
 *
 * When this creature enters, draw a card.
 */
val KavuClimber = card("Kavu Climber") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Kavu"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "192"
        artist = "Rob Alexander"
        flavorText = "The appearance of the first kavu surprised Multani. As they continued to emerge, he no longer had any doubts about Yavimaya's ability to defend herself."
        imageUri = "https://cards.scryfall.io/normal/front/2/0/2063f31e-d972-411e-a265-1d409153b49c.jpg?1562901445"
    }
}
