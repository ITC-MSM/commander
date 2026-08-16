package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Malcator's Watcher
 * {1}{U}
 * Artifact Creature — Phyrexian Drone
 * 1/1
 *
 * Flying, vigilance
 * When this creature dies, draw a card.
 */
val MalcatorsWatcher = card("Malcator's Watcher") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Phyrexian Drone"
    power = 1
    toughness = 1
    oracleText = "Flying, vigilance\n" +
        "When this creature dies, draw a card."

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "58"
        artist = "Jason A. Engle"
        imageUri = "https://cards.scryfall.io/normal/front/9/5/95a7e5ff-ba81-4b45-933e-0ac747525ab8.jpg?1783918062"
    }
}
