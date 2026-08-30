package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scalding Devil
 * {1}{R}
 * Creature — Devil
 * 1 / 1
 *
 * {2}{R}: This creature deals 1 damage to target player or planeswalker.
 */
val ScaldingDevil = card("Scalding Devil") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Devil"
    power = 1
    toughness = 1
    oracleText = "{2}{R}: This creature deals 1 damage to target player or planeswalker."

    activatedAbility {
        cost = Costs.Mana("{2}{R}")
        val victim = target("target", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(1, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "155"
        artist = "Erica Yang"
        flavorText = "Demons massacre. Devils annoy."
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bbe49a97-dac8-4273-b4dc-45cdf8f5a6e0.jpg?1783940677"
    }
}
