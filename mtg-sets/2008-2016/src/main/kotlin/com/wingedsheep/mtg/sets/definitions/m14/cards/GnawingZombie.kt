package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Gnawing Zombie
 * {1}{B}
 * Creature — Zombie
 * 1/3
 * {1}{B}, Sacrifice a creature: Target player loses 1 life and you gain 1 life.
 */
val GnawingZombie = card("Gnawing Zombie") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 1
    toughness = 3
    oracleText = "{1}{B}, Sacrifice a creature: Target player loses 1 life and you gain 1 life."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{B}"),
            Costs.Sacrifice(GameObjectFilter.Creature)
        )
        val player = target("player", Targets.Player)
        effect = Effects.Composite(
            Effects.LoseLife(1, player),
            Effects.GainLife(1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "99"
        artist = "Greg Staples"
        flavorText = "On still nights you can hear its rotted teeth grinding tirelessly on scavenged bones."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56653d9e-0c29-440b-8724-cae746abb1a9.jpg?1783939924"
    }
}
