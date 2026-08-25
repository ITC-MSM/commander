package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stony Strength — Ravnica Allegiance #143
 * {G} · Instant
 *
 * A permanent +1/+1 counter rather than an until-end-of-turn pump, plus an untap — the untap
 * is what makes it a combat trick on a blocker that has already been tapped for mana.
 */
val StonyStrength = card("Stony Strength") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Put a +1/+1 counter on target creature you control. Untap that creature."

    spell {
        val creature = target("target", Targets.CreatureYouControl)
        effect = Effects.Composite(listOf(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature),
            Effects.Untap(creature)
        ))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "143"
        artist = "Chris Seaman"
        flavorText = "\"What you build, we will destroy . . . and bury you in the rubble!\""
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8bbab274-69dd-44a9-9310-a15779c35cad.jpg"
    }
}
