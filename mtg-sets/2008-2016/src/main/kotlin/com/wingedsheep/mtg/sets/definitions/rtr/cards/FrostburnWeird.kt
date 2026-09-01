package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Frostburn Weird
 * {U/R}{U/R}
 * Creature — Weird
 * 1/4
 *
 * {U/R}: This creature gets +1/-1 until end of turn.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * A hybrid-mana pump on itself. The negative toughness modifier is a layer-7c effect like any
 * other, so repeated activations can kill the Weird.
 */
val FrostburnWeird = card("Frostburn Weird") {
    manaCost = "{U/R}{U/R}"
    colorIdentity = "RU"
    typeLine = "Creature — Weird"
    oracleText = "{U/R}: This creature gets +1/-1 until end of turn."
    power = 1
    toughness = 4

    activatedAbility {
        cost = Costs.Mana("{U/R}")
        effect = Effects.ModifyStats(1, -1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "215"
        artist = "Mike Bierek"
        flavorText = "Many chemisters are oblivious to the innumerable machinations of their guild, instead focusing obsessively on creating the perfect weird."
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba5a68d3-6bc9-4de8-bc06-e1106cf9b3d4.jpg?1783940327"
    }
}
