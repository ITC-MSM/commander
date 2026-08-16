package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Chandra's Magmutt
 * {1}{R}
 * Creature — Elemental Dog
 * 2/2
 * {T}: This creature deals 1 damage to target player or planeswalker.
 *
 * A tap-only activated ability. [Targets.PlayerOrPlaneswalker] is the narrower "any target"
 * minus creatures/battles, so a creature can never be chosen.
 */
val ChandrasMagmutt = card("Chandra's Magmutt") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Dog"
    power = 2
    toughness = 2
    oracleText = "{T}: This creature deals 1 damage to target player or planeswalker."

    activatedAbility {
        cost = Costs.Tap
        val victim = target("player or planeswalker", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(1, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "137"
        artist = "Kimonas Theodossiou"
        flavorText = "\"Is it purebred? No, but it's pure fire.\" —Chandra Nalaar"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/91d3e366-4da5-42c8-bbd5-a0c178c0da28.jpg?1783930694"
    }
}
