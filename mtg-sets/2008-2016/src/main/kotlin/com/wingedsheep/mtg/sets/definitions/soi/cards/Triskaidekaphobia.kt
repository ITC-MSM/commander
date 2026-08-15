package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Triskaidekaphobia
 * {3}{B}
 * Enchantment
 * At the beginning of your upkeep, choose one —
 * • Each player with exactly 13 life loses the game, then each player gains 1 life.
 * • Each player with exactly 13 life loses the game, then each player loses 1 life.
 *
 * "Each player with exactly 13 life loses the game" is a [ForEachPlayerEffect] (APNAP order) whose
 * body rebinds the controller context to the iterated player, so [DynamicAmount.LifeTotal] of
 * [Player.You] reads *that* player's life (see Pox Plague) — a per-player [ConditionalEffect]
 * makes them lose the game when it equals 13. Both modes share that clause; only the trailing
 * "each player gains / loses 1 life" differs.
 */
val Triskaidekaphobia = card("Triskaidekaphobia") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, choose one —\n" +
        "• Each player with exactly 13 life loses the game, then each player gains 1 life.\n" +
        "• Each player with exactly 13 life loses the game, then each player loses 1 life."

    triggeredAbility {
        trigger = Triggers.YourUpkeep

        val eachPlayerWith13LosesTheGame = ForEachPlayerEffect(
            Player.ActivePlayerFirst,
            listOf(
                ConditionalEffect(
                    condition = Conditions.CompareAmounts(
                        DynamicAmount.LifeTotal(Player.You),
                        ComparisonOperator.EQ,
                        DynamicAmount.Fixed(13)
                    ),
                    effect = Effects.LoseGame()
                )
            )
        )

        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                eachPlayerWith13LosesTheGame.then(
                    Effects.GainLife(1, EffectTarget.PlayerRef(Player.Each))
                ),
                "Each player with exactly 13 life loses the game, then each player gains 1 life."
            ),
            Mode.noTarget(
                eachPlayerWith13LosesTheGame.then(
                    Effects.LoseLife(1, EffectTarget.PlayerRef(Player.Each))
                ),
                "Each player with exactly 13 life loses the game, then each player loses 1 life."
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "141"
        artist = "Willian Murai"
        imageUri = "https://cards.scryfall.io/normal/front/8/0/8060b717-94c9-4962-9676-0b1ca6a357a8.jpg?1782712061"
    }
}
