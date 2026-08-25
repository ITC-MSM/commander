package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Telekinetic Sliver
 * {2}{U}{U}
 * Creature — Sliver
 * 2/2
 * All Slivers have "{T}: Tap target permanent."
 */
val TelekineticSliver = card("Telekinetic Sliver") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Slivers have \"{T}: Tap target permanent.\""

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.Tap(EffectTarget.BoundVariable("target")),
                targetRequirements = listOf(
                    TargetObject(
                        filter = TargetFilter(GameObjectFilter.Permanent),
                        id = "target"
                    )
                )
            ),
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "84"
        artist = "Randy Elliott"
        flavorText = "\"Slivers are guided only by simple instinct. Advance the hive, and you will be welcomed. Impede the hive, and you will face unrelenting opposition.\"\n—Freyalise"
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61b934ad-4858-4680-924a-53ea4f250f9e.jpg"
    }
}
