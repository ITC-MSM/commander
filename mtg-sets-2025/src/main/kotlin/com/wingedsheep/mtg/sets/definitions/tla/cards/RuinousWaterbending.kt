package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ruinous Waterbending
 * {1}{B}{B}
 * Sorcery — Lesson
 * As an additional cost to cast this spell, you may waterbend {4}. (While paying a waterbend cost,
 * you can tap your artifacts and creatures to help. Each one pays for {1}.)
 * All creatures get -2/-2 until end of turn. If this spell's additional cost was paid, whenever a
 * creature dies this turn, you gain 1 life.
 */
val RuinousWaterbending = card("Ruinous Waterbending") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery — Lesson"
    oracleText = "As an additional cost to cast this spell, you may waterbend {4}. " +
        "(While paying a waterbend cost, you can tap your artifacts and creatures to help. " +
        "Each one pays for {1}.)\n" +
        "All creatures get -2/-2 until end of turn. If this spell's additional cost was paid, " +
        "whenever a creature dies this turn, you gain 1 life."

    waterbendCost(amount = 4, optional = true)

    spell {
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                filter = GroupFilter.AllCreatures,
                effect = ModifyStatsEffect(-2, -2, EffectTarget.Self)
            ),
            // If the optional waterbend was paid, set up a this-turn delayed trigger that gains
            // 1 life each time a creature dies (the mass -2/-2 deaths happen as SBAs after this
            // spell finishes resolving, so the trigger is in place to catch them).
            ConditionalEffect(
                condition = Conditions.WaterbendWasPaid,
                effect = CreateDelayedTriggerEffect(
                    trigger = Triggers.AnyCreatureDies,
                    effect = Effects.GainLife(1),
                    expiry = DelayedTriggerExpiry.EndOfTurn,
                    fireOnce = false
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "118"
        artist = "Yuu Fujiki"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53161747-c82b-41e8-90ea-7791ea262a85.jpg?1764120817"
    }
}
