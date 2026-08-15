package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Unravel
 * {1}{U}{U}
 * Instant
 * Counter target spell. If the amount of mana spent to cast that spell was less
 * than its mana value, you draw a card.
 *
 * The mana-value-vs-paid comparison only matters when the targeted spell had a
 * cost reduction (affinity, convoke, delve, etc.); per the Scryfall ruling,
 * cost reductions/increases don't change mana value (CR 202.3).
 */
val Unravel = card("Unravel") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell. If the amount of mana spent to cast that spell was less than its mana value, you draw a card."

    spell {
        target = Targets.Spell
        effect = ConditionalEffect(
            condition = Compare(
                left = DynamicAmounts.targetManaSpent(),
                operator = ComparisonOperator.LT,
                right = DynamicAmounts.targetManaValue()
            ),
            effect = Effects.CounterSpell().then(Effects.DrawCards(1)),
            elseEffect = Effects.CounterSpell()
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "83"
        artist = "Josh Hass"
        flavorText = "Drix weftwalking requires neither eternity columns nor Pinnacle codebooks. But the art is ancient, and each passing is a leap of faith."
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8978214-c853-453d-872d-af56bdaaa3d7.jpg?1752946891"
        ruling("2025-07-25", "The mana value of a spell isn't changed by alternative costs, cost increases, or cost reductions. For example, if you cast Thrumming Hivepool (an artifact with affinity for Slivers), its mana value is 6 no matter how many Slivers you controlled when you cast it.")
        ruling("2025-07-25", "For spells with {X} in their mana costs, use the value chosen for X to determine the spell's mana value.")
    }
}
