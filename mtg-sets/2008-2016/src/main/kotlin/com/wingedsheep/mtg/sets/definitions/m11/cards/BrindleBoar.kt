package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Brindle Boar
 * {2}{G}
 * Creature — Boar
 * 2/2
 *
 * Sacrifice this creature: You gain 4 life.
 *
 * - Sacrificing is the whole cost ([Costs.SacrificeSelf]), so the ability can be activated at
 *   instant speed, needs no mana, and still gains the life if the boar is removed in response —
 *   the cost is paid on activation.
 */
val BrindleBoar = card("Brindle Boar") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Boar"
    power = 2
    toughness = 2
    oracleText = "Sacrifice this creature: You gain 4 life."

    activatedAbility {
        cost = Costs.SacrificeSelf
        effect = Effects.GainLife(4)
        description = "Sacrifice this creature: You gain 4 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "166"
        artist = "Dave Allsop"
        flavorText = "The war lasted for generations. The boars didn't need to hunt for food anymore. They fed on the fallen corpses, and the living fed off of them."
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2bc665c-d507-4de3-a8e8-731cc8487840.jpg?1783941800"
    }
}
