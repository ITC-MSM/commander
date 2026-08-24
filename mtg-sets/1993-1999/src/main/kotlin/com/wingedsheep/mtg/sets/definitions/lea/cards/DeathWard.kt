package com.wingedsheep.mtg.sets.definitions.lea.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect

/**
 * Death Ward
 * {W}
 * Instant
 *
 * Regenerate target creature.
 *
 * There is no `Effects.Regenerate` facade — [RegenerateEffect] is the shipped spelling (Reknit,
 * Crypt Sliver) — and it is target-type-agnostic, so pointing it at `Targets.Creature` is the whole
 * card. Ice Age reprints it; the canonical definition lives here, in its Alpha printing.
 */
val DeathWard = card("Death Ward") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Regenerate target creature."

    spell {
        val t = target("target", Targets.Creature)
        effect = RegenerateEffect(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "Mark Poole"
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa5466cc-aa57-4a7f-8b21-d92b2fe02e13.jpg"
    }
}
