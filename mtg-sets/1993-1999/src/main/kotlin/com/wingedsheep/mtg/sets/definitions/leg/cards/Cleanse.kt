package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Cleanse
 * {2}{W}{W}
 * Sorcery
 *
 * Destroy all black creatures.
 */
val Cleanse = card("Cleanse") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy all black creatures."

    spell {
        effect = Effects.DestroyAll(GameObjectFilter.Creature.withColor(Color.BLACK))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "5"
        artist = "Phil Foglio"
        flavorText = "The clouds broke and the sun's rays burst forth; each foul beast in its turn faltered, and " +
            "was gone."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2fbd611b-ac97-4516-bad7-cc9ee4ef74f7.jpg?1783948087"
    }
}
