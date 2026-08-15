package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Llanowar Envoy
 * {2}{G}
 * Creature — Elf Scout
 * 3/2
 * {1}{G}: Add one mana of any color.
 */
val LlanowarEnvoy = card("Llanowar Envoy") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Scout"
    power = 3
    toughness = 2
    oracleText = "{1}{G}: Add one mana of any color."

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Daniel Ljunggren"
        flavorText = "\"Cherish this world in honor of the martyrs who saved it. We too must be prepared to give our lives.\" —The Mending of Dominaria"
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a667bba-ecf0-4212-8ca3-75d4db6abce2.jpg?1593862645"
    }
}
