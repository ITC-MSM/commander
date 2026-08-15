package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Fear of Immobility
 * {4}{W}
 * Enchantment Creature — Nightmare
 * 4/4
 * When this creature enters, tap up to one target creature. If an opponent controls that creature,
 * put a stun counter on it. (If a permanent with a stun counter would become untapped, remove one
 * from it instead.)
 *
 * "Up to one target creature" → an optional [TargetCreature]; the creature is always tapped, and
 * the stun counter is gated at resolution on the target being controlled by an opponent via
 * [Conditions.TargetMatchesFilter] over [GameObjectFilter.Creature.opponentControls]. Tapping a
 * creature you control gives no stun counter, matching the printed text.
 */
val FearOfImmobility = card("Fear of Immobility") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Nightmare"
    power = 4
    toughness = 4
    oracleText = "When this creature enters, tap up to one target creature. If an opponent controls " +
        "that creature, put a stun counter on it. (If a permanent with a stun counter would become " +
        "untapped, remove one from it instead.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(optional = true))
        effect = Effects.Composite(
            Effects.Tap(t),
            ConditionalEffect(
                condition = Conditions.TargetMatchesFilter(GameObjectFilter.Creature.opponentControls()),
                effect = AddCountersEffect(counterType = Counters.STUN, count = 1, target = t),
            ),
        )
        description = "When this creature enters, tap up to one target creature. If an opponent " +
            "controls that creature, put a stun counter on it."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Martin de Diego Sádaba"
        flavorText = "As it slunk toward her, Nina told her legs to run, but they stayed rooted and " +
            "rigid. She screamed—but no sound left her lips."
        imageUri = "https://cards.scryfall.io/normal/front/9/2/9220c8fa-6ef7-4bc1-acb9-fc54cc43e498.jpg?1726285895"
    }
}
