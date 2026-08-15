package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Food Fight
 * {1}{R}
 * Enchantment
 *
 * Artifacts you control have "{2}, Sacrifice this artifact: It deals damage to any target equal
 * to 1 plus the number of permanents named Food Fight you control."
 */
val FoodFight = card("Food Fight") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Artifacts you control have \"{2}, Sacrifice this artifact: It deals damage to any target " +
        "equal to 1 plus the number of permanents named Food Fight you control.\""

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(Costs.Mana("{2}"), Costs.SacrificeSelf),
                effect = Effects.DealDamage(
                    DynamicAmount.Add(
                        DynamicAmount.Fixed(1),
                        DynamicAmount.Count(
                            Player.You,
                            Zone.BATTLEFIELD,
                            GameObjectFilter.Any.named("Food Fight")
                        )
                    ),
                    EffectTarget.ContextTarget(0),
                    damageSource = EffectTarget.Self
                ),
                targetRequirement = AnyTarget()
            ),
            filter = GroupFilter(GameObjectFilter.Artifact.youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "129"
        artist = "Filipe Pagliuso"
        flavorText = "With no time to gather weapons, the dwarves fought the redcaps with anything within reach."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a7cc43c-6e8c-41d2-a885-24604dfc7e7f.jpg"
    }
}
