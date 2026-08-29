package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Final Death
 * {4}{B}
 * Instant
 *
 * Exile target creature.
 *
 * One sentence, so the spell effect is the bare [Effects.Exile] move rather than a single-element
 * composite. No `fromZone` guard: the target is a battlefield permanent bound at cast time.
 */
val FinalDeath = card("Final Death") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Exile target creature."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Exile(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Johann Bodin"
        flavorText = "The Underworld erodes memory, identity, and eventually the physical form, " +
            "leaving only crumbling statues called misera—hollow monuments to mortal futility."
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e5b8580-9198-4735-83c1-289400c1d814.jpg"
    }
}
