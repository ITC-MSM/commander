package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Centaur's Herald
 * {G}
 * Creature — Elf Scout
 * 0/1
 *
 * {2}{G}, Sacrifice this creature: Create a 3/3 green Centaur creature token.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * [Costs.SacrificeSelf] is the cost atom for "Sacrifice this creature" — paid on activation, so
 * the token arrives whether or not the ability is countered.
 */
val CentaursHerald = card("Centaur's Herald") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Scout"
    oracleText = "{2}{G}, Sacrifice this creature: Create a 3/3 green Centaur creature token."
    power = 0
    toughness = 1

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{G}"), Costs.SacrificeSelf)
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Centaur"),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Howard Lyon"
        flavorText = "The farther they go from Vitu-Ghazi, the less willing the crowd is to part for them."
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08598b2b-6fd2-4a1d-8d74-7ca6d93ad382.jpg?1783940350"
    }
}
