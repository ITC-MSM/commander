package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fell {1}{B}
 * Sorcery
 *
 * Destroy target creature.
 */
val Fell = card("Fell") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature."

    spell {
        val creature = target("target creature to destroy", Targets.Creature)
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "95"
        artist = "A. M. Sartor"
        flavorText = "When Glarb ordered the egg stolen, the Great Maha communicated its anger in a universal language."
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c96ac326-de44-470b-a592-a4c2a052c091.jpg?1721426418"
    }
}
