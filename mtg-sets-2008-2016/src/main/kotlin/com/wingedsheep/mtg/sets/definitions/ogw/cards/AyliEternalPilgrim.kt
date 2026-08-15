package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ayli, Eternal Pilgrim
 * {W}{B}
 * Legendary Creature — Kor Cleric
 * 2/3
 *
 * Deathtouch
 * {1}, Sacrifice another creature: You gain life equal to the sacrificed creature's toughness.
 * {1}{W}{B}, Sacrifice another creature: Exile target nonland permanent. Activate only if
 * you have at least 10 life more than your starting life total.
 *
 * The life-gain ability reuses [DynamicAmounts.sacrificedToughness] (same shape as Kheru
 * Dreadmaw). The exile ability is gated with [ActivationRestriction.OnlyIfCondition] on a
 * [Compare] of your life total against starting-life-total + 10 (same shape as Leyline of
 * Hope's static ability).
 */
val AyliEternalPilgrim = card("Ayli, Eternal Pilgrim") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Kor Cleric"
    power = 2
    toughness = 3
    oracleText = "Deathtouch\n" +
        "{1}, Sacrifice another creature: You gain life equal to the sacrificed creature's toughness.\n" +
        "{1}{W}{B}, Sacrifice another creature: Exile target nonland permanent. Activate only if you have at least 10 life more than your starting life total."

    keywords(Keyword.DEATHTOUCH)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.SacrificeAnother(GameObjectFilter.Creature)
        )
        effect = Effects.GainLife(DynamicAmounts.sacrificedToughness())
        description = "You gain life equal to the sacrificed creature's toughness."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{W}{B}"),
            Costs.SacrificeAnother(GameObjectFilter.Creature)
        )
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Compare(
                    left = DynamicAmount.LifeTotal(Player.You),
                    operator = ComparisonOperator.GTE,
                    right = DynamicAmount.Add(
                        DynamicAmount.StartingLifeTotal(Player.You),
                        DynamicAmount.Fixed(10)
                    )
                )
            )
        )
        val t = target("target", Targets.NonlandPermanent)
        effect = Effects.Exile(t)
        description = "Exile target nonland permanent."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "151"
        artist = "Cynthia Sheppard"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7b5893d-6df6-4cae-ae70-d02d443d1740.jpg?1783937898"
    }
}
