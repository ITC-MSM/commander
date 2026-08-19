package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Zombie Infestation
 * {1}{B}
 * Enchantment
 *
 * Discard two cards: Create a 2/2 black Zombie creature token.
 */
val ZombieInfestation = card("Zombie Infestation") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Discard two cards: Create a 2/2 black Zombie creature token."

    activatedAbility {
        cost = Costs.Discard(count = 2)
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Zombie"),
        )
        description = "Discard two cards: Create a 2/2 black Zombie creature token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "170"
        artist = "Thomas M. Baxa"
        flavorText = "The nomads' funeral pyres are more practical than ceremonial."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/ccd5f98a-7ab5-44b3-850c-b50963dace66.jpg?1783945237"
    }
}
