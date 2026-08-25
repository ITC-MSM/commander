package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Select for Inspection
 * {U}
 * Instant
 * Return target tapped creature to its owner's hand. Scry 1. (Look at the top card of your
 * library. You may put that card on the bottom.)
 *
 * "Tapped" is part of the target requirement rather than a check at resolution, so it uses
 * [Targets.TappedCreature] — the creature must be tapped when the spell is cast and again when it
 * resolves. The scry is unconditional: it happens even if the bounce fizzles on its own.
 */
val SelectForInspection = card("Select for Inspection") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target tapped creature to its owner's hand. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        val t = target("target", Targets.TappedCreature)
        effect = Effects.Composite(
            Effects.ReturnToHand(t),
            Effects.Scry(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "63"
        artist = "James Paick"
        imageUri = "https://cards.scryfall.io/normal/front/1/2/120f0fb0-4831-4759-b58c-05c4be90a4af.jpg?1783937216"
    }
}
