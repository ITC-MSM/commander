package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Grasping Dunes
 *
 * Land — Desert
 * {T}: Add {C}.
 * {1}, {T}, Sacrifice this land: Put a -1/-1 counter on target creature. Activate only as a sorcery.
 *
 * "Activate only as a sorcery" is [TimingRule.SorcerySpeed]. The second ability targets, so it is
 * not a mana ability even though the first one is.
 */
val GraspingDunes = card("Grasping Dunes") {
    typeLine = "Land — Desert"
    oracleText = "{T}: Add {C}.\n" +
        "{1}, {T}, Sacrifice this land: Put a -1/-1 counter on target creature. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeSelf)
        val creature = target("target", Targets.Creature)
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, creature)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "244"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8fcc939-6a31-4fb3-abe7-7663b85868dd.jpg?1783936446"
    }
}
