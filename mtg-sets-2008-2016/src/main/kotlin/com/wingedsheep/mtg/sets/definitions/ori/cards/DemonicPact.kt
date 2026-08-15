package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Demonic Pact
 * {2}{B}{B}
 * Enchantment
 * At the beginning of your upkeep, choose one that hasn't been chosen —
 * • This enchantment deals 4 damage to any target and you gain 4 life.
 * • Target opponent discards two cards.
 * • Draw two cards.
 * • You lose the game.
 *
 * Modelling notes:
 * - "Choose one that hasn't been chosen" is [ModalEffect.chooseOneNotYetChosen]: the mode memory is
 *   keyed to this specific permanent (CR 700.4 object identity), which matches the rulings — a
 *   second copy of Demonic Pact starts fresh, and a new controller inherits the modes already
 *   chosen rather than resetting them.
 * - The mode is picked as the triggered ability goes on the stack, and a chosen mode counts as
 *   chosen even if the ability later fizzles or is countered — both fall out of the shared modal
 *   machinery, which commits the pick at trigger-put-on-stack time.
 * - Once the first three modes are gone the fourth is the only legal pick, so the pact collects
 *   (the printed trap). If even that has been chosen the ability leaves the stack doing nothing.
 */
val DemonicPact = card("Demonic Pact") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, choose one that hasn't been chosen —\n" +
        "• This enchantment deals 4 damage to any target and you gain 4 life.\n" +
        "• Target opponent discards two cards.\n" +
        "• Draw two cards.\n" +
        "• You lose the game."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = ModalEffect.chooseOneNotYetChosen(
            // • This enchantment deals 4 damage to any target and you gain 4 life.
            Mode.withTarget(
                Effects.Composite(
                    Effects.DealDamage(4, EffectTarget.ContextTarget(0), damageSource = EffectTarget.Self),
                    Effects.GainLife(4)
                ),
                Targets.Any,
                "This enchantment deals 4 damage to any target and you gain 4 life"
            ),
            // • Target opponent discards two cards.
            Mode.withTarget(
                Effects.Discard(2, EffectTarget.ContextTarget(0)),
                Targets.Opponent,
                "Target opponent discards two cards"
            ),
            // • Draw two cards.
            Mode.noTarget(
                Effects.DrawCards(2),
                "Draw two cards"
            ),
            // • You lose the game.
            Mode.noTarget(
                Effects.LoseGame(),
                "You lose the game"
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "92"
        artist = "Aleksi Briclot"
        imageUri = "https://cards.scryfall.io/normal/front/8/2/82c04014-91f9-4197-b4b4-f62c4739a5c2.jpg?1783938343"
        ruling(
            "2015-06-22",
            "You choose the mode as the triggered ability goes on the stack. You can choose a mode " +
                "that requires targets only if there are legal targets available."
        )
        ruling(
            "2015-06-22",
            "If the ability doesn't resolve (either for having its target become illegal or because " +
                "a spell or ability counters it), the mode chosen for that instance of the ability " +
                "still counts as being chosen."
        )
        ruling(
            "2015-06-22",
            "The phrase \"that hasn't been chosen\" refers only to that specific Demonic Pact. If you " +
                "control one and cast another one, you can choose any mode for the second one the " +
                "first time its ability triggers."
        )
        ruling(
            "2015-06-22",
            "It doesn't matter who has chosen any particular mode. For example, say you control " +
                "Demonic Pact and have chosen the first two modes. If an opponent gains control of " +
                "Demonic Pact, that player can choose only the third or fourth mode."
        )
        ruling(
            "2015-06-22",
            "In some very unusual situations, you may not be able to choose a mode, either because " +
                "all modes have previously been chosen or the only remaining modes require targets " +
                "and there are no legal targets available. In this case, the ability is simply " +
                "removed from the stack with no effect."
        )
        ruling(
            "2015-06-22",
            "Yes, if the fourth mode is the only one remaining, you must choose it. You read the " +
                "whole contract, right?"
        )
    }
}
