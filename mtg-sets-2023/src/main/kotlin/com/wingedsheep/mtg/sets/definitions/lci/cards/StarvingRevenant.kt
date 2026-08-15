package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Starving Revenant (LCI #123) — {2}{B}{B} Creature — Spirit Horror (rare), 4/4.
 *
 * When this creature enters, surveil 2. Then for each card you put on top of your library, you
 * draw a card and you lose 3 life.
 * Descend 8 — Whenever you draw a card, if there are eight or more permanent cards in your
 * graveyard, target opponent loses 1 life and you gain 1 life.
 *
 * ETB "surveil-then-draw-per-card-kept" — composed entirely from existing primitives:
 *  - [Effects.Surveil] (2) runs the sanctioned surveil macro, whose expanded pipeline stores the
 *    cards the controller keeps ON TOP under the pipeline collection `"toTop"` (the surveil
 *    remainder) and fires "whenever you surveil" triggers. The macro executes in the *same*
 *    [com.wingedsheep.engine.handlers.EffectContext], so `"toTop"` is visible to the sibling
 *    effects that follow it in this Composite (CompositeEffectExecutor threads storedCollections
 *    across children, including across the surveil selection pause).
 *  - The count of cards kept on top is read with [DynamicAmount.DistinctEntitiesInCollections]
 *    over `"toTop"`. `MoveCollection` never clears its source collection, so the value is still
 *    correct after those cards have been moved onto the library. That count drives both the draw
 *    (`draw that many`) and, via [DynamicAmount.Multiply] × 3, the life loss (`lose 3 per card`).
 *  - Keeping zero on top ⇒ count 0 ⇒ draw 0 and lose 0 (both no-op), matching the rules.
 *
 * The ETB draws feed the card's own descend trigger below: each drawn card that resolves while
 * eight-or-more permanent cards sit in the graveyard puts a drain on the stack.
 *
 * Descend 8 (draw drain) — a "whenever you draw a card" ([Triggers.YouDraw], fires once per card
 * drawn) triggered ability gated by an intervening-if ([TriggeredAbilityBuilder.triggerCondition]
 * = [Conditions.CardsInGraveyardMatchingAtLeast] (8, [GameObjectFilter.Permanent])). Per CR 603.4
 * the condition is checked both when the ability would trigger and again on resolution, so it does
 * nothing if the graveyard drops below eight permanent cards in between. "target opponent loses 1
 * life" is a fixed [Effects.LoseLife] on the bound opponent target; "you gain 1 life" is a fixed
 * [Effects.GainLife] on the controller — two separate fixed amounts, not a drain.
 */
val StarvingRevenant = card("Starving Revenant") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit Horror"
    power = 4
    toughness = 4
    oracleText = "When this creature enters, surveil 2. Then for each card you put on top of your " +
        "library, you draw a card and you lose 3 life.\n" +
        "Descend 8 — Whenever you draw a card, if there are eight or more permanent cards in your " +
        "graveyard, target opponent loses 1 life and you gain 1 life."

    // ETB: surveil 2, then for each card kept on top, draw one and lose 3 life.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            listOf(
                Effects.Surveil(2),
                Effects.DrawCards(DynamicAmount.DistinctEntitiesInCollections(listOf("toTop"))),
                Effects.LoseLife(
                    DynamicAmount.Multiply(DynamicAmount.DistinctEntitiesInCollections(listOf("toTop")), 3),
                    EffectTarget.Controller
                )
            )
        )
    }

    // Descend 8: whenever you draw a card, if eight or more permanent cards are in your graveyard,
    // target opponent loses 1 life and you gain 1 life.
    triggeredAbility {
        trigger = Triggers.YouDraw
        triggerCondition = Conditions.CardsInGraveyardMatchingAtLeast(8, GameObjectFilter.Permanent)
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.Composite(
            listOf(
                Effects.LoseLife(1, opponent),
                Effects.GainLife(1)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "123"
        artist = "Fesbra"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f25ea466-eb48-4c4c-b5d4-35f58e46ebe1.jpg?1783913770"
    }
}
