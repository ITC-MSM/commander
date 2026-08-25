package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Unfriendly Fire
 * {4}{R}
 * Instant
 *
 * Unfriendly Fire deals 4 damage to any target.
 */
val UnfriendlyFire = card("Unfriendly Fire") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Unfriendly Fire deals 4 damage to any target."

    spell {
        val victim = target("target", Targets.Any)
        effect = Effects.DealDamage(4, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "172"
        artist = "Josu Hernaiz"
        flavorText = "Disputes within the Brazen Coalition can escalate from insult to broadside in the blink of an eye."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a61b274-0499-4cb6-a2e4-f5e18ad7fd2d.jpg"
    }
}
