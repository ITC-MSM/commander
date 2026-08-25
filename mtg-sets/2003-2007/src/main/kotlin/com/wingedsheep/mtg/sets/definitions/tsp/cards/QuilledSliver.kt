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
 * Quilled Sliver
 * {1}{W}
 * Creature — Sliver
 * 1/1
 * All Slivers have "{T}: This permanent deals 1 damage to target attacking or blocking creature."
 */
val QuilledSliver = card("Quilled Sliver") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Slivers have \"{T}: This permanent deals 1 damage to target attacking or blocking creature.\""

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.DealDamage(1, EffectTarget.BoundVariable("target")),
                targetRequirements = listOf(
                    TargetObject(
                        filter = TargetFilter(GameObjectFilter.Creature.attackingOrBlocking()),
                        id = "target"
                    )
                )
            ),
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "37"
        artist = "John Matson"
        flavorText = "\"They have long kept us under attack, but we do not lack for ammunition. The very bodies of our foes arm us against them.\"\n—Adom Capashen, Benalish hero"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72486240-eabb-4b37-99cc-ab13413683fa.jpg"
    }
}
