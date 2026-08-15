package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Comforting Counsel
 * {1}{G}
 * Enchantment
 *
 * Whenever you gain life, put a growth counter on this enchantment.
 * As long as there are five or more growth counters on this enchantment, creatures you control
 * get +3/+3.
 *
 * Same shape as Beastmaster Ascension: a [Triggers.YouGainLife] trigger that drops a [Counters.GROWTH]
 * counter on [EffectTarget.Self], plus a counter-gated anthem. The threshold gate is the generic
 * [Conditions.SourceCounterCountAtLeast] (≥5 growth counters), which reads the enchantment's counters
 * live, and the buff applies to all creatures you control via [GroupFilter.AllCreaturesYouControl].
 */
val ComfortingCounsel = card("Comforting Counsel") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Whenever you gain life, put a growth counter on this enchantment.\n" +
        "As long as there are five or more growth counters on this enchantment, creatures you control get +3/+3."

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.AddCounters(Counters.GROWTH, 1, EffectTarget.Self)
    }

    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(Counters.GROWTH, 5)
        ability = ModifyStats(
            powerBonus = 3,
            toughnessBonus = 3,
            filter = GroupFilter.AllCreaturesYouControl
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "143"
        artist = "Chris Rahn"
        flavorText = "In the grief that followed the Invasion, Willowdusk's office turned into an unofficial hideaway for overwhelmed students."
        imageUri = "https://cards.scryfall.io/normal/front/5/2/5223a04f-6b47-4379-80ce-8489c4a91734.jpg?1775937970"
    }
}
