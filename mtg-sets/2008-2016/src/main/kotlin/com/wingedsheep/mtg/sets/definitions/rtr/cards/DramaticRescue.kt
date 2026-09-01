package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dramatic Rescue
 * {W}{U}
 * Instant
 *
 * Return target creature to its owner's hand. You gain 2 life.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * A bounce plus an untargeted life rider, composed.
 */
val DramaticRescue = card("Dramatic Rescue") {
    manaCost = "{W}{U}"
    colorIdentity = "UW"
    typeLine = "Instant"
    oracleText = "Return target creature to its owner's hand. You gain 2 life."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.ReturnToHand(t),
            Effects.GainLife(2),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "156"
        artist = "Ryan Pancoast"
        flavorText = "\"I never thought I'd be so glad to see two tons of beak and claws bearing down on me at the speed of an arrow.\"\n" +
            "—Mirela, Azorius hussar"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/041afd23-1ecc-4cca-9244-fe42203ad689.jpg?1783940342"
    }
}
