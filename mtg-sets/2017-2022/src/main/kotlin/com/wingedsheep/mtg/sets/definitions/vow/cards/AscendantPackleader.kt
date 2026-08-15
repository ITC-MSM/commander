package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ascendant Packleader
 * {G}
 * Creature — Wolf
 * 2/1
 *
 * This creature enters with a +1/+1 counter on it if you control a permanent with mana
 * value 4 or greater.
 * Whenever you cast a spell with mana value 4 or greater, put a +1/+1 counter on this creature.
 *
 * The conditional enters-with-counter clause is a self-only [EntersWithCounters] replacement
 * gated on the intervening condition "you control a permanent with mana value 4 or greater"
 * (CR 614). The cast trigger is [Triggers.youCastSpell] filtered to mana value >= 4; because
 * the spell's mana value is fixed on the stack, the filter reads it directly with no cast-time
 * choice involved.
 */
val AscendantPackleader = card("Ascendant Packleader") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 2
    toughness = 1
    oracleText = "This creature enters with a +1/+1 counter on it if you control a permanent " +
        "with mana value 4 or greater.\n" +
        "Whenever you cast a spell with mana value 4 or greater, put a +1/+1 counter on this creature."

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.PlusOnePlusOne,
            count = 1,
            selfOnly = true,
            condition = Conditions.YouControl(GameObjectFilter.Permanent.manaValueAtLeast(4))
        )
    )

    triggeredAbility {
        trigger = Triggers.youCastSpell(spellFilter = GameObjectFilter.Any.manaValueAtLeast(4))
        effect = AddCountersEffect(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            count = 1,
            target = EffectTarget.Self
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "186"
        artist = "Alessandra Pisano"
        imageUri = "https://cards.scryfall.io/normal/front/1/4/142c5b67-5de9-41da-b57f-7ce771457a3e.jpg?1783924820"
    }
}
