package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Aryel, Knight of Windgrace
 * {2}{W}{B}
 * Legendary Creature — Human Knight
 * 4/4
 * Vigilance
 * {2}{W}, {T}: Create a 2/2 white Knight creature token with vigilance.
 * {B}, {T}, Tap X untapped Knights you control: Destroy target creature with power X or less.
 */
val AryelKnightOfWindgrace = card("Aryel, Knight of Windgrace") {
    manaCost = "{2}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Human Knight"
    power = 4
    toughness = 4
    oracleText = "Vigilance\n{2}{W}, {T}: Create a 2/2 white Knight creature token with vigilance.\n{B}, {T}, Tap X untapped Knights you control: Destroy target creature with power X or less."

    keywords(Keyword.VIGILANCE)

    // {2}{W}, {T}: Create a 2/2 white Knight creature token with vigilance.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{W}"), Costs.Tap)
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Knight"),
            keywords = setOf(Keyword.VIGILANCE)
        )
    }

    // {B}, {T}, Tap X untapped Knights you control: Destroy target creature with power X or less.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{B}"),
            Costs.Tap,
            Costs.TapXPermanents(GameObjectFilter.Creature.withSubtype("Knight"))
        )
        val creature = target("creature", Targets.Creature)
        // At resolution, destroy the target only if its power <= X
        effect = ConditionalEffect(
            condition = Compare(
                left = DynamicAmounts.targetPower(0),
                operator = ComparisonOperator.LTE,
                right = DynamicAmount.XValue
            ),
            effect = Effects.Destroy(creature)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "192"
        artist = "Grzegorz Rutkowski"
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58e2981b-bf25-4d9d-8811-5a1ff0b4d5d2.jpg?1562736077"
        ruling("2018-04-27", "Tapping Aryel to activate either of its abilities while it's attacking doesn't remove it from combat.")
        ruling("2018-04-27", "Once you announce that you're activating Aryel's last ability, no player may take other actions until the ability's been paid for. Notably, opponents can't try to remove Knights you'd like to tap to pay the cost.")
        ruling("2018-04-27", "If the target creature's power is greater than X as the last ability resolves, the ability does nothing.")
    }
}
