package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PreventionScope

/**
 * Safeguard
 * {3}{W}{W}
 * Enchantment
 * {2}{W}: Prevent all combat damage that would be dealt by target creature this turn.
 */
val Safeguard = card("Safeguard") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "{2}{W}: Prevent all combat damage that would be dealt by target creature this turn."

    activatedAbility {
        cost = Costs.Mana("{2}{W}")
        val creature = target("target", Targets.Creature)
        effect = Effects.PreventAllDamageDealtBy(creature, scope = PreventionScope.CombatOnly)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "39"
        artist = "Thomas M. Baxa"
        flavorText = "\"I'm amused by wasted effort when it's not my own.\"\n" +
            "—Hanna, *Weatherlight* navigator"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c8e174c-7abb-4a93-aa1d-8c2a2e815ba6.jpg"
    }
}
