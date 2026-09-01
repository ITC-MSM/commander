package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Perilous Shadow
 * {2}{B}{B}
 * Creature — Insect Shade
 * 0/4
 *
 * {1}{B}: This creature gets +2/+2 until end of turn.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * The Shade firebreathing pattern: a repeatable self-pump, defaulting to end of turn.
 */
val PerilousShadow = card("Perilous Shadow") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect Shade"
    oracleText = "{1}{B}: This creature gets +2/+2 until end of turn."
    power = 0
    toughness = 4

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "74"
        artist = "Clint Cearley"
        flavorText = "There are some shadows that even the Dimir fear."
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c101171-a988-4c1d-9954-634e2f1c6f01.jpg?1783940361"
    }
}
