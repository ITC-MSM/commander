package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.AddDynamicCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Jenova, Ancient Calamity — Final Fantasy #228
 * {2}{B}{G}
 * Legendary Creature — Alien
 * 1/5
 *
 * At the beginning of combat on your turn, put a number of +1/+1 counters equal to Jenova's
 * power on up to one other target creature. That creature becomes a Mutant in addition to its
 * other types.
 * Whenever a Mutant you control dies during your turn, you draw cards equal to its power.
 *
 * The combat trigger uses [Triggers.BeginCombat]; the target is "up to one other target
 * creature" — an optional single [TargetCreature] over [TargetFilter.OtherCreature]
 * (excludeSelf), so the whole effect no-ops when no target is chosen (cf. Ardyn, the Usurper).
 * The counter amount is [DynamicAmounts.sourcePower] (Jenova's current power) and the type
 * grant is a permanent [Effects.AddCreatureType] ("in addition to its other types").
 *
 * The dies payoff mirrors Rakdos Joins Up: a [Triggers.leavesBattlefield] (… to GRAVEYARD)
 * over Mutant creatures you control, gated to "during your turn" via
 * triggerCondition = [Conditions.IsYourTurn]. The draw amount is [DynamicAmounts.triggeringPower],
 * which resolves via last-known information for the just-died creature.
 */
val JenovaAncientCalamity = card("Jenova, Ancient Calamity") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Alien"
    power = 1
    toughness = 5
    oracleText = "At the beginning of combat on your turn, put a number of +1/+1 counters equal to " +
        "Jenova's power on up to one other target creature. That creature becomes a Mutant in addition " +
        "to its other types.\n" +
        "Whenever a Mutant you control dies during your turn, you draw cards equal to its power."

    triggeredAbility {
        trigger = Triggers.BeginCombat
        // "Up to one other target" — both the counters and the type grant no-op if no target
        // is chosen, which faithfully matches "up to one".
        val t = target(
            "up to one other target creature",
            TargetCreature(optional = true, filter = TargetFilter.OtherCreature)
        )
        effect = Effects.Composite(listOf(
            AddDynamicCountersEffect(Counters.PLUS_ONE_PLUS_ONE, DynamicAmounts.sourcePower(), t),
            Effects.AddCreatureType("Mutant", t)
        ))
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.withSubtype("Mutant").youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        triggerCondition = Conditions.IsYourTurn
        effect = Effects.DrawCards(DynamicAmounts.triggeringPower())
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "228"
        artist = "Ignatius Budi"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/534f98ee-7bc2-44d6-b49f-f57c051807d5.jpg?1748706622"
    }
}
