package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedActivatedAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Case of the Burning Masks — Murders at Karlov Manor #113
 * {1}{R}{R} · Enchantment — Case · Uncommon
 *
 * When this Case enters, it deals 3 damage to target creature an opponent controls.
 * To solve — Three or more sources you controlled dealt damage this turn.
 * Solved — Sacrifice this Case: Exile the top three cards of your library. Choose one of them.
 * You may play that card this turn.
 *
 * "Three or more **sources**" counts objects, not damage events, which is what
 * [Conditions.SourcesYouControlledDealtDamageThisTurn] reads: the engine records each source on
 * the player who controlled it *at the moment it dealt damage*, so all four printed rulings hold —
 * a source that dies or changes hands afterwards still counts, a creature that deals damage three
 * times counts once, an ability is not itself a source (its source is the object it came from),
 * and a permanent that left the battlefield and came back is a new object and counts again. The
 * Case's own enters trigger is one of the three.
 *
 * The Solved ability is impulse-draw-with-a-choice: exile three, then choose exactly one, and only
 * that one gets the play permission. The other two stay exiled and unplayable, which is why this
 * isn't `Patterns.Exile.impulse(3)` (that grants permission to all of them).
 */
val CaseOfTheBurningMasks = card("Case of the Burning Masks") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Case"
    oracleText = "When this Case enters, it deals 3 damage to target creature an opponent " +
        "controls.\n" +
        "To solve — Three or more sources you controlled dealt damage this turn. (If unsolved, " +
        "solve at the beginning of your end step.)\n" +
        "Solved — Sacrifice this Case: Exile the top three cards of your library. Choose one of " +
        "them. You may play that card this turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = TargetCreature(filter = TargetFilter.Creature.opponentControls())
        effect = Effects.DealDamage(3, EffectTarget.ContextTarget(0))
    }

    toSolve(Conditions.SourcesYouControlledDealtDamageThisTurn(3))

    solvedActivatedAbility {
        cost = Costs.SacrificeSelf
        effect = Effects.Pipeline {
            val exiled = gather(CardSource.TopOfLibrary(DynamicAmount.Fixed(3)))
            exile(exiled)
            val chosen = chooseExactly(
                count = 1,
                from = exiled,
                filter = GameObjectFilter.Any,
                prompt = "Choose a card you may play this turn",
                showAllCards = true
            )
            run(GrantMayPlayFromExileEffect(chosen.key, MayPlayExpiry.EndOfTurn))
        }
        description = "Sacrifice this Case: Exile the top three cards of your library. Choose one " +
            "of them. You may play that card this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "113"
        artist = "Bastien L. Deharme"
        imageUri = "https://cards.scryfall.io/normal/front/2/9/29ee07df-215f-45a6-9a5a-708143d73e45.jpg?1783912887"

        ruling(
            "2024-02-09",
            "You need to control the sources that dealt damage only at the times when they dealt " +
                "damage. If they die or change control after that point, the \"to solve\" ability " +
                "will still trigger at the beginning of your end step as long as you still control " +
                "Case of the Burning Masks."
        )
        ruling(
            "2024-02-09",
            "Activated and triggered abilities are not themselves sources of damage. The source of " +
                "an activated ability is the object whose ability was activated, and the source of " +
                "a triggered ability (other than a delayed triggered ability) is the object whose " +
                "ability triggered."
        )
        ruling(
            "2024-02-09",
            "If multiple creatures you control deal combat damage, each one counts as a source " +
                "that dealt damage that turn. A single creature that deals combat damage multiple " +
                "times in a turn (due to double strike or additional combats) still counts as only " +
                "one source."
        )
        ruling(
            "2024-02-09",
            "You pay all costs and follow all normal timing rules for the card played from exile " +
                "with Case of the Burning Masks's last ability. For example, if the exiled card is " +
                "a land card, you may play it only during your main phase while the stack is empty."
        )
    }
}
