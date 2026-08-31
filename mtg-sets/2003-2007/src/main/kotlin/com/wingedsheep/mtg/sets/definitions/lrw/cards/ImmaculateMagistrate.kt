package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Immaculate Magistrate
 * {3}{G}
 * Creature — Elf Shaman
 * 2/2
 * {T}: Put a +1/+1 counter on target creature for each Elf you control.
 *
 * The count is a [Effects.AddDynamicCounters] amount, so it is read when the ability *resolves*
 * (2020-11-10 ruling) rather than on activation — Elves arriving or leaving in response change
 * how many counters land. The target is any creature, not just an Elf (same ruling), and the
 * Magistrate itself is an Elf, so it always contributes at least one.
 */
val ImmaculateMagistrate = card("Immaculate Magistrate") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Shaman"
    power = 2
    toughness = 2
    oracleText = "{T}: Put a +1/+1 counter on target creature for each Elf you control."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature", Targets.Creature)
        effect = Effects.AddDynamicCounters(
            Counters.PLUS_ONE_PLUS_ONE,
            DynamicAmounts.battlefield(
                Player.You,
                GameObjectFilter.Permanent.withSubtype(Subtype.ELF)
            ).count(),
            creature
        )
        description = "{T}: Put a +1/+1 counter on target creature for each Elf you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "219"
        artist = "Jim Nelson"
        flavorText = "Elves of the immaculate class weave flora into living creatures—sometimes " +
            "to endorse an elite warrior, sometimes to create a breathing work of art."
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3bbf6137-4f50-4915-b608-7a03512476a2.jpg?1783942860"
        ruling("2020-11-10", "The number of counters to put on the target creature is determined " +
            "only as Immaculate Magistrate's ability resolves. Elves coming and going later won't " +
            "cause that creature to gain or lose +1/+1 counters.")
        ruling("2020-11-10", "The target creature doesn't have to be an Elf.")
    }
}
