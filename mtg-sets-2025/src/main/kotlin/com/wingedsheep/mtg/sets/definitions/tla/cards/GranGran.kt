package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Gran-Gran
 * {U}
 * Legendary Creature — Human Peasant Ally
 * 1/2
 * Whenever Gran-Gran becomes tapped, draw a card, then discard a card.
 * Noncreature spells you cast cost {1} less to cast as long as there are three or more
 * Lesson cards in your graveyard.
 */
val GranGran = card("Gran-Gran") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Peasant Ally"
    power = 1
    toughness = 2
    oracleText = "Whenever Gran-Gran becomes tapped, draw a card, then discard a card.\n" +
        "Noncreature spells you cast cost {1} less to cast as long as there are three or more Lesson cards in your graveyard."

    triggeredAbility {
        trigger = Triggers.BecomesTapped
        effect = Patterns.Hand.loot(draw = 1, discard = 1)
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifySpellCost(
                target = SpellCostTarget.YouCast(GameObjectFilter.Noncreature),
                modification = CostModification.ReduceGeneric(1),
            ),
            condition = Conditions.CompareAmounts(
                DynamicAmount.Count(
                    Player.You,
                    Zone.GRAVEYARD,
                    GameObjectFilter.Any.withSubtype(Subtype.LESSON),
                ),
                ComparisonOperator.GTE,
                DynamicAmount.Fixed(3),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "54"
        artist = "Arou"
        flavorText = "\"It's been so long since I've had hope, but you brought it back to life.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa434b41-e5f7-4989-865a-95db67b05cb1.jpg?1764120269"
    }
}
