package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Pyroconvergence
 * {4}{R}
 * Enchantment
 *
 * Whenever you cast a multicolored spell, this enchantment deals 2 damage to any target.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Lobber Crew's trigger with a targeted payoff. The target is chosen when the ability goes on the
 * stack, which is *above* the multicolored spell that triggered it, so the damage resolves first.
 */
val Pyroconvergence = card("Pyroconvergence") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Whenever you cast a multicolored spell, this enchantment deals 2 damage to any target."

    triggeredAbility {
        trigger = Triggers.youCastSpell(GameObjectFilter.Multicolored)
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "103"
        artist = "Jack Wang"
        flavorText = "\"The Izzet are an equation that turns lunacy into explosions.\"\n" +
            "—Leonos, Azorius arbiter"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6cff95b7-79eb-4796-9a01-31ff355681ab.jpg?1783940353"
    }
}
