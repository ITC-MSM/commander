package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Avengers Assemble! — Marvel Super Heroes #6 (mythic)
 * {4}{W} · Enchantment
 *
 * Flash
 * Heroes you control get +2/+2.
 * At the beginning of each end step, if you attacked with a Hero this turn or a Hero entered
 * the battlefield under your control this turn, draw a card.
 *
 * Implementation notes:
 * - The anthem is a Layer 7c [ModifyStats] over a [GroupFilter] of Heroes you control. No
 *   `excludeSelf` is needed — the enchantment isn't a Hero.
 * - The draw is [Triggers.EachEndStep] (each player's end step, not just yours) with an
 *   intervening-if (CR 603.4): the condition is checked both when the trigger would fire and
 *   again on resolution, which is what "if …" before the effect means.
 * - **Known approximation.** Both halves of the intervening-if are battlefield existence checks
 *   (`attackedThisTurn` / `enteredThisTurn` state predicates over Heroes you control), not
 *   turn-history trackers. A Hero that attacked (or entered) and has since left the battlefield,
 *   changed controller, or stopped being a Hero therefore no longer satisfies the condition,
 *   whereas the printed card only cares that the event happened. The engine has no
 *   subtype-scoped "attacked this turn" / "entered this turn" player tracker to key off —
 *   `Conditions.YouAttackedThisTurn` is untyped and
 *   `Conditions.PermanentTypeEnteredBattlefieldThisTurn` keys on card type, not subtype.
 */
val AvengersAssemble = card("Avengers Assemble!") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Flash\n" +
        "Heroes you control get +2/+2.\n" +
        "At the beginning of each end step, if you attacked with a Hero this turn or a Hero " +
        "entered the battlefield under your control this turn, draw a card."

    keywords(Keyword.FLASH)

    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 2,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.HERO).youControl()),
        )
    }

    triggeredAbility {
        trigger = Triggers.EachEndStep
        triggerCondition = Conditions.Any(
            Conditions.YouControl(
                GameObjectFilter.Permanent.withSubtype(Subtype.HERO).attackedThisTurn(),
            ),
            Conditions.YouControl(
                GameObjectFilter.Permanent.withSubtype(Subtype.HERO).enteredThisTurn(),
            ),
        )
        effect = Effects.DrawCards(1)
        description = "At the beginning of each end step, if you attacked with a Hero this turn " +
            "or a Hero entered the battlefield under your control this turn, draw a card."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "6"
        artist = "Alex Horley-Orlandelli"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf736399-af74-4f52-9159-67ea67d0cf83.jpg?1783902981"
    }
}
