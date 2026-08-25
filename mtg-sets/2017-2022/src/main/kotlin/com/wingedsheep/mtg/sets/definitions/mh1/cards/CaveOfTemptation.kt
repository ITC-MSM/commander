package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Cave of Temptation
 * Land
 * {T}: Add {C}.
 * {1}, {T}: Add one mana of any color.
 * {4}, {T}, Sacrifice this land: Put two +1/+1 counters on target creature. Activate only as a sorcery.
 *
 * "Activate only as a sorcery" is [TimingRule.SorcerySpeed], not a separate restriction. The third
 * ability targets, so it is not a mana ability even though the land's other two are.
 */
val CaveOfTemptation = card("Cave of Temptation") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{1}, {T}: Add one mana of any color.\n" +
        "{4}, {T}, Sacrifice this land: Put two +1/+1 counters on target creature. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.AddManaOfChoice()
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap, Costs.SacrificeSelf)
        val creature = target("target", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, creature)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "237"
        artist = "Winona Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d86e9149-6fd9-44fc-b765-3e646c7d83d6.jpg?1783933069"
    }
}
