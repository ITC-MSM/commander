package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Growth Curve
 * {G}{U}
 * Sorcery
 * Put a +1/+1 counter on target creature you control, then double the number of +1/+1 counters
 * on that creature.
 *
 * Composed from [Effects.AddCounters] (one +1/+1 counter) then [Effects.DoubleCounters] on the same
 * target. The "then" ordering means the freshly-added counter is included in the doubling (e.g. a
 * creature with two +1/+1 counters becomes three, then six).
 */
val GrowthCurve = card("Growth Curve") {
    manaCost = "{G}{U}"
    colorIdentity = "UG"
    typeLine = "Sorcery"
    oracleText = "Put a +1/+1 counter on target creature you control, then double the number of " +
        "+1/+1 counters on that creature."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.youControl()))
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t),
            Effects.DoubleCounters(Counters.PLUS_ONE_PLUS_ONE, t)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "193"
        artist = "Joe Slucher"
        flavorText = "\"There is no force stronger or more daunting than that of exponential growth.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/6/1675a445-86ae-413b-b95a-a1c254a7f252.jpg?1775938339"
    }
}
