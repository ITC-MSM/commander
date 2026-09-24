package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Jace, Reality Sculptor
 * {3}{U}{U}
 * Legendary Planeswalker — Jace
 * Starting Loyalty: 5
 *
 * - +1 is Empower Jace with a dynamic X — the number of Islands you control, read as the ability
 *   resolves.
 * - −3 installs a per-attacker delayed trigger that lives until your next turn
 *   ([DelayedTriggerExpiry.UntilControllersNextTurn]); it is Jace's delayed ability, so it keeps
 *   working if Jace leaves. Each creature declared attacking you or a planeswalker you control
 *   triggers it once, and the −5/−0 goes on that attacker (the triggering entity). A creature
 *   attacking a battle, or a teammate, doesn't trigger it.
 * - 0 counts loyalty counters among every Jace you control (this one, Jace tokens from empower, and
 *   any other Jace) as an activation restriction. Its effect is Doomsday Excruciator's "all but the
 *   bottom N" gather, per opponent: the top (library size − 1) cards, clamped at zero, exiled face
 *   up.
 */
val JaceRealitySculptor = card("Jace, Reality Sculptor") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Planeswalker — Jace"
    startingLoyalty = 5
    oracleText = "+1: Empower Jace X, where X is the number of Islands you control.\n" +
        "−3: Until your next turn, whenever a creature attacks you or a planeswalker you control, " +
        "it gets -5/-0 until end of turn.\n" +
        "0: Exile all but the bottom card of each opponent's library. Activate only if there are " +
        "twenty-five or more loyalty counters among Jaces you control."

    loyaltyAbility(+1) {
        effect = Patterns.Mechanic.empowerJace(
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Land.withSubtype("Island")).count()
        )
        description = "Empower Jace X, where X is the number of Islands you control."
    }

    loyaltyAbility(-3) {
        effect = CreateDelayedTriggerEffect(
            trigger = TriggerSpec(
                event = EventPattern.AttackEvent(
                    filter = GameObjectFilter.Creature.attackingYouOrYourPlaneswalkers(),
                ),
                binding = TriggerBinding.ANY,
            ),
            effect = Effects.ModifyStats(-5, 0, EffectTarget.TriggeringEntity),
            expiry = DelayedTriggerExpiry.UntilControllersNextTurn,
        )
        description = "Until your next turn, whenever a creature attacks you or a planeswalker you " +
            "control, it gets -5/-0 until end of turn."
    }

    loyaltyAbility(0) {
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.CounterKindAmongYouControlAtLeast(
                    count = 25,
                    counterType = CounterTypeFilter.Loyalty,
                    filter = GameObjectFilter.Planeswalker.withSubtype("Jace"),
                )
            )
        )
        effect = ForEachPlayerEffect(
            players = Player.EachOpponent,
            effects = listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(
                        DynamicAmount.IfPositive(
                            DynamicAmount.Subtract(
                                DynamicAmount.Count(Player.You, Zone.LIBRARY),
                                DynamicAmount.Fixed(1),
                            )
                        )
                    ),
                    storeAs = "realitySculptorExiled",
                ),
                MoveCollectionEffect(
                    from = "realitySculptorExiled",
                    destination = CardDestination.ToZone(Zone.EXILE),
                ),
            ),
        )
        description = "Exile all but the bottom card of each opponent's library. Activate only if " +
            "there are twenty-five or more loyalty counters among Jaces you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "216"
        artist = "Martina Fačková"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74087795-0b38-4fd2-9841-147583baca41.jpg?1789644880"
        inBooster = false
    }
}
