package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Crawling Barrens — ZNR #262
 * Land — Rare
 *
 * {T}: Add {C}.
 * {4}: Put two +1/+1 counters on this land. Then you may have it become a 0/0 Elemental
 *   creature until end of turn. It's still a land.
 *
 * The counters go on *first* and stay on the permanent even while it isn't a creature
 * (per the ZNR rulings), so the animate half never produces a 0/0 that dies to state-based
 * actions — by the time it is a creature it already has at least two +1/+1 counters.
 * [Effects.BecomeCreature] is additive in Layer 4, so the Land type is preserved
 * ("It's still a land"). The animate half is a genuine "may": the counters are placed even
 * if the player declines.
 */
val CrawlingBarrens = card("Crawling Barrens") {
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{4}: Put two +1/+1 counters on this land. Then you may have it become a 0/0 " +
        "Elemental creature until end of turn. It's still a land."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{4}")
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self),
            MayEffect(
                effect = Effects.BecomeCreature(
                    target = EffectTarget.Self,
                    power = 0,
                    toughness = 0,
                    creatureTypes = setOf("Elemental"),
                    duration = Duration.EndOfTurn,
                ),
            ),
        )
        description = "Put two +1/+1 counters on this land. Then you may have it become a " +
            "0/0 Elemental creature until end of turn. It's still a land."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "262"
        artist = "Jonas De Ro"
        flavorText = "Zendikar is restless."
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7bd0e025-7a75-4641-a51a-27df9dcde05f.jpg?1783929307"

        ruling("2024-11-08", "You can activate Crawling Barrens's second ability even if it's already a creature.")
        ruling(
            "2024-11-08",
            "Counters on Crawling Barrens remain on it when it stops being a creature. If it becomes a " +
                "creature later, they'll apply to it.",
        )
        ruling(
            "2024-11-08",
            "Unless Crawling Barrens is already a creature, its second ability causes the +1/+1 counters " +
                "to be put onto a noncreature land. Abilities that apply or trigger when counters are put " +
                "on a creature you control won't do so.",
        )
    }
}
