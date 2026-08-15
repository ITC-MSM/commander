package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.Aggregation
import com.wingedsheep.sdk.scripting.values.CardNumericProperty
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Orysa, Tide Choreographer
 * {4}{U}
 * Legendary Creature — Merfolk Bard
 * 2/2
 *
 * This spell costs {3} less to cast if creatures you control have total toughness 10 or greater.
 * When Orysa enters, draw two cards.
 *
 * The cost reduction is a fixed {3} gated on an intervening condition (CR 601.2f, the
 * cost-determination step reads the battlefield), modelled with [ModifySpellCost] +
 * [CostGating.OnlyIf]. The condition sums the projected toughness of creatures you control via
 * [DynamicAmount.AggregateBattlefield] (SUM over TOUGHNESS), so layer/counter modifications are
 * respected.
 */
val OrysaTideChoreographer = card("Orysa, Tide Choreographer") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Merfolk Bard"
    power = 2
    toughness = 2
    oracleText = "This spell costs {3} less to cast if creatures you control have total toughness " +
        "10 or greater.\nWhen Orysa enters, draw two cards."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(CostReductionSource.Fixed(3)),
            gating = CostGating.OnlyIf(
                Conditions.CompareAmounts(
                    DynamicAmount.AggregateBattlefield(
                        player = Player.You,
                        filter = GameObjectFilter.Creature,
                        aggregation = Aggregation.SUM,
                        property = CardNumericProperty.TOUGHNESS,
                    ),
                    ComparisonOperator.GTE,
                    DynamicAmount.Fixed(10),
                ),
            ),
        )
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(2)
        description = "When Orysa enters, draw two cards."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "Anna Pavleeva"
        flavorText = "Orysa and her troupe of turtles are a staple of Summitfest's daytime celebrations."
        imageUri = "https://cards.scryfall.io/normal/front/0/1/010ed379-63f5-452c-9cd4-00d51647c0e3.jpg?1775937343"
    }
}
