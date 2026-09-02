package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Big Play — Strixhaven: School of Mages #122 (canonical printing)
 * {1}{G} · Instant
 *
 * Target creature gets +2/+2 and gains reach until end of turn. Put a +1/+1 counter on it. (A creature with reach can block creatures with flying.)
 *
 * The Snare the Skies pump with a counter rider, all on one target. The first sentence is its own
 * [Effects.Composite] of [Effects.ModifyStats] and [Effects.GrantKeyword] (both until end of
 * turn), and that composite is then followed by the permanent [Effects.AddCounters] — nested
 * explicitly rather than chained with `then`, which would flatten the first sentence away.
 */
val BigPlay = card("Big Play") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText =
        "Target creature gets +2/+2 and gains reach until end of turn. Put a +1/+1 counter on it. (A creature with reach can block creatures with flying.)"

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.Composite(
                Effects.ModifyStats(2, 2, creature),
                Effects.GrantKeyword(Keyword.REACH, creature)
            ),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "122"
        artist = "Nicholas Gregory"
        flavorText = "\"Quandrix is running out of time. If they're going to capture the Witherbloom mascot, they need something big here.\"\n—Cremik, Mage Tower commentator"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/9016d667-50a9-4093-9a99-b34dcdafe60b.jpg?1783927347"
    }
}
