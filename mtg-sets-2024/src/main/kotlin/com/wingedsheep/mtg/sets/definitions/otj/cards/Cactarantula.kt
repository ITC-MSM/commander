package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Cactarantula
 * {4}{G}{G}
 * Creature — Plant Spider
 * 6/5
 * This spell costs {1} less to cast if you control a Desert.
 * Reach
 * Whenever this creature becomes the target of a spell or ability an opponent controls,
 * you may draw a card.
 */
val Cactarantula = card("Cactarantula") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Spider"
    power = 6
    toughness = 5
    oracleText = "This spell costs {1} less to cast if you control a Desert.\nReach\nWhenever this creature becomes the target of a spell or ability an opponent controls, you may draw a card."

    keywords(Keyword.REACH)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfControlFilter(
                    amount = 1,
                    filter = GameObjectFilter.Land.withSubtype(Subtype.DESERT),
                ),
            ),
        )
    }

    triggeredAbility {
        trigger = Triggers.BecomesTargetByOpponent
        optional = true
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "Filip Burburan"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e0e27f9-dc2c-4366-b810-3e8d0bdff8c3.jpg?1712355900"

        ruling("2024-04-12", "Cactarantula still costs only {1} less to cast if you control multiple Deserts.")
        ruling("2024-04-12", "Cactarantula's last ability resolves before the spell or ability that caused it to trigger. It resolves even if that spell is countered.")
        ruling("2024-04-12", "If a spell or ability targets Cactarantula more than once, its last ability still triggers only once.")
    }
}
