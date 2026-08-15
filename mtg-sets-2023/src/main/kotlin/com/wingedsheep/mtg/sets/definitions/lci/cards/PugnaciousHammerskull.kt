package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pugnacious Hammerskull
 * {2}{G}
 * Creature — Dinosaur
 * 6/6
 * Whenever this creature attacks while you don't control another Dinosaur, put a stun counter on
 * it. (If a permanent with a stun counter would become untapped, remove one from it instead.)
 *
 * Implementation notes:
 * - `Triggers.Attacks` fires per AttackEvent for the creature itself (SELF binding).
 * - The intervening-if (CR 603.4) is the negated, self-excluding control check
 *   `Conditions.YouControl(Creature Dinosaur, negate = true, excludeSelf = true)`:
 *   "you don't control another Dinosaur" — the Hammerskull itself is excluded from the search,
 *   and `negate` inverts the existence test. Checked both when the trigger would go on the stack
 *   and again on resolution; if either check finds another Dinosaur, the trigger doesn't fire (or
 *   is removed before resolving).
 * - `Effects.AddCounters(Counters.STUN, 1, EffectTarget.Self)` places one stun counter on the
 *   Hammerskull, keeping it from untapping (CR 122.1d / stun counter replacement).
 */
val PugnaciousHammerskull = card("Pugnacious Hammerskull") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    power = 6
    toughness = 6
    oracleText = "Whenever this creature attacks while you don't control another Dinosaur, put a stun counter on it. (If a permanent with a stun counter would become untapped, remove one from it instead.)"

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.YouControl(
            GameObjectFilter.Creature.withSubtype(Subtype.DINOSAUR),
            negate = true,
            excludeSelf = true
        )
        effect = Effects.AddCounters(Counters.STUN, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "208"
        artist = "Kev Walker"
        flavorText = "\"I don't know whether it hates the mycoids or just loves breaking things. I say we leave it be.\"\n—Samin, Oltec scout"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/632e5635-a9bc-473a-a885-02e1fd258f7b.jpg?1782694442"
    }
}
