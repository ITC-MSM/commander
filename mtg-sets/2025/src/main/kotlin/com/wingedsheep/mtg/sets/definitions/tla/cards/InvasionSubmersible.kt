package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Invasion Submersible
 * {2}{U}
 * Artifact — Vehicle
 * 0/0
 *
 * When this Vehicle enters, return up to one other target nonland permanent to its owner's hand.
 * Exhaust — Waterbend {3}: This Vehicle becomes an artifact creature. Put three +1/+1 counters on it.
 *   (Activate each exhaust ability only once.)
 *
 * The ETB bounces up to one other nonland permanent. The exhaust ability (isExhaust = true) carries a
 * waterbend cost (hasWaterbend = true — tap artifacts/creatures to help pay the {3}) and composes
 * [Effects.AddCardType] "Creature" (permanently turning the 0/0 Vehicle into an artifact creature)
 * with three +1/+1 counters, leaving a 3/3.
 */
val InvasionSubmersible = card("Invasion Submersible") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Vehicle"
    power = 0
    toughness = 0
    oracleText = "When this Vehicle enters, return up to one other target nonland permanent to its owner's hand.\n" +
        "Exhaust — Waterbend {3}: This Vehicle becomes an artifact creature. Put three +1/+1 counters on it. " +
        "(While paying a waterbend cost, you can tap your artifacts and creatures to help. Each one pays for {1}. " +
        "Activate each exhaust ability only once.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target(
            "up to one other target nonland permanent",
            TargetPermanent(optional = true, filter = TargetFilter.NonlandPermanent.other())
        )
        effect = Effects.ReturnToHand(permanent)
        description = "When this Vehicle enters, return up to one other target nonland permanent to its owner's hand."
    }

    activatedAbility {
        isExhaust = true
        hasWaterbend = true
        cost = Costs.Mana("{3}")
        effect = Effects.Composite(
            Effects.AddCardType("Creature", EffectTarget.Self),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "57"
        artist = "Sylvain Sarrailh"
        imageUri = "https://cards.scryfall.io/normal/front/a/f/af5299f5-1633-4c07-ade5-fd47e29ea4aa.jpg?1764120298"
    }
}
