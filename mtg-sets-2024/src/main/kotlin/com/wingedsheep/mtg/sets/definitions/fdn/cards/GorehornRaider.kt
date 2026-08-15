package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gorehorn Raider
 * {4}{R}
 * Creature — Minotaur Pirate
 * 4/4
 *
 * Raid — When this creature enters, if you attacked this turn, this creature
 * deals 2 damage to any target.
 *
 * Raid is modeled as an enters-the-battlefield trigger gated by the intervening-if
 * condition [Conditions.YouAttackedThisTurn] (mirrors Mardu Heart-Piercer). The
 * condition is re-checked on resolution, so the trigger does nothing if the
 * "you attacked this turn" state no longer holds.
 */
val GorehornRaider = card("Gorehorn Raider") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Pirate"
    power = 4
    toughness = 4
    oracleText = "Raid — When this creature enters, if you attacked this turn, this creature deals 2 damage to any target."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.YouAttackedThisTurn
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/7/8/78ce6c40-3452-4aa0-a45b-dbfd70f8d220.jpg?1782689188"
    }
}
