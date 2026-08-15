package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Buzz Bots
 * {1}{U}
 * Artifact Creature — Robot Insect
 * 1/1
 *
 * Flying, vigilance
 * When this creature dies, draw a card.
 */
val BuzzBots = card("Buzz Bots") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Robot Insect"
    oracleText = "Flying, vigilance\nWhen this creature dies, draw a card."
    power = 1
    toughness = 1

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Néstor Ossandón Leal"
        flavorText = "Designed for autonomous pollination, the Stocktronics buzz bot's souped-up power plant allows for months of independent service. The optional taser is for defensive purposes only, of course."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c375190-f81b-4ab1-a1b6-fe432796821f.jpg?1771502556"
    }
}
