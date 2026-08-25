package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Spontaneous Flight
 * {2}{W}
 * Instant
 * Target creature gets +2/+2 until end of turn. Put a flying counter on it.
 *
 * Two effects on one target: the pump expires at end of turn, the flying counter does not. A
 * flying counter is a keyword counter (CR 122.1b / 613.1f) that the projection maps to the
 * keyword, so the evasion sticks around long after the +2/+2 has worn off.
 */
val SpontaneousFlight = card("Spontaneous Flight") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 until end of turn. Put a flying counter on it."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, t),
            Effects.AddCounters(Counters.FLYING, 1, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Gabor Szikszai"
        flavorText = "\"Amazing! And I was only trying to teach her to sit!\""
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3dc24dd4-d259-4687-8247-f56aa7abb5b9.jpg"
    }
}
