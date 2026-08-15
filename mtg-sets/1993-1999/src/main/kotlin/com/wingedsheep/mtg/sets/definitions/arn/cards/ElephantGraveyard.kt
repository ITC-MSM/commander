package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Elephant Graveyard
 * Land
 * {T}: Add {C}.
 * {T}: Regenerate target Elephant.
 */
val ElephantGraveyard = card("Elephant Graveyard") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Add {C}.\n{T}: Regenerate target Elephant."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        val elephant = target(
            "target Elephant",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.withSubtype("Elephant")))
        )
        effect = RegenerateEffect(elephant)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "74"
        artist = "Rob Alexander"
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18348df2-9037-4db4-bddb-76dc933229bf.jpg?1562899580"
    }
}
