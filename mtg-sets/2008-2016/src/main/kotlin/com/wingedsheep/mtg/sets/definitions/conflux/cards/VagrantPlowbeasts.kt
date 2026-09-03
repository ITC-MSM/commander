package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Vagrant Plowbeasts
 * {5}{G}{W}
 * Creature — Beast
 * 6/6
 * {1}: Regenerate target creature with power 5 or greater.
 *
 * [RegenerateEffect] is the shipped spelling — there is no `Effects.Regenerate` facade. The
 * "power 5 or greater" clause is `GameObjectFilter.Creature.powerAtLeast(5)`, a card predicate the
 * targeting check re-reads off projected state, so a creature pumped into range is a legal target.
 */
val VagrantPlowbeasts = card("Vagrant Plowbeasts") {
    manaCost = "{5}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Beast"
    power = 6
    toughness = 6
    oracleText = "{1}: Regenerate target creature with power 5 or greater."

    activatedAbility {
        cost = Costs.Mana("{1}")
        val creature = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.powerAtLeast(5)))
        )
        effect = RegenerateEffect(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "129"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "Plowbeasts of Naya escaped their harnesses in droves, content to snack on the conveniently cultivated fields of Eos and Valeron."
        imageUri = "https://cards.scryfall.io/normal/front/5/4/546b0a74-ebef-4596-b730-2190e20b2e66.jpg"
    }
}
