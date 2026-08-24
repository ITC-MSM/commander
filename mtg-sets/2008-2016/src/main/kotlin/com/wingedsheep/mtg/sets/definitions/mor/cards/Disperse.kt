package com.wingedsheep.mtg.sets.definitions.mor.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Disperse
 * {1}{U}
 * Instant
 *
 * Return target nonland permanent to its owner's hand.
 */
val Disperse = card("Disperse") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target nonland permanent to its owner's hand."

    spell {
        val t = target("target nonland permanent", Targets.NonlandPermanent)
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31"
        artist = "Steve Ellis"
        flavorText = "Gryffid scowled at the sky. A perfect day for the hunt tainted by clouds. He wished them gone. High above, the clouds looked down, scowled, and made a wish of their own."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0ae239b2-1596-4906-9711-1d180a246d35.jpg?1783942800"
    }
}
