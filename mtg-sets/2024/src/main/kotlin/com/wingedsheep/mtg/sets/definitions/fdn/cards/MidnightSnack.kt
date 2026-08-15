package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Midnight Snack
 * {2}{B}
 * Enchantment
 *
 * Raid — At the beginning of your end step, if you attacked this turn, create a Food token.
 * ({2}, {T}, Sacrifice this token: You gain 3 life.)
 * {2}{B}, Sacrifice this enchantment: Target opponent loses X life, where X is the amount of
 * life you gained this turn.
 *
 * "Raid" is an ability word (no rules meaning); the actual gate is the intervening-if condition
 * that you attacked this turn, evaluated both when the trigger would be put on the stack and
 * again on resolution ([Conditions.YouAttackedThisTurn]). The drain reads the LIFE_GAINED turn
 * tracker via [DynamicAmounts.lifeGainedThisTurn] — if you gained no life this turn, X = 0.
 */
val MidnightSnack = card("Midnight Snack") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Raid — At the beginning of your end step, if you attacked this turn, create a " +
        "Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")\n" +
        "{2}{B}, Sacrifice this enchantment: Target opponent loses X life, where X is the amount " +
        "of life you gained this turn."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouAttackedThisTurn
        effect = Effects.CreateFood()
        description = "At the beginning of your end step, if you attacked this turn, create a Food token."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{B}"),
            Costs.SacrificeSelf
        )
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.LoseLife(DynamicAmounts.lifeGainedThisTurn(), opponent)
        description = "Target opponent loses X life, where X is the amount of life you gained this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "65"
        artist = "Kai Carpenter"
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c9b7543f-2a45-4db6-b560-d15507a58c91.jpg?1782689208"
    }
}
