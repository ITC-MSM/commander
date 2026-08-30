package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Convolute
 * {2}{U}
 * Instant
 *
 * Counter target spell unless its controller pays {4}.
 */
val Convolute = card("Convolute") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell unless its controller pays {4}."

    spell {
        target("target spell", Targets.Spell)
        effect = Effects.CounterUnlessPays("{4}")
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Dany Orizio"
        flavorText = "The words came to the sorcerer's lips but refused to budge any further."
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fac88052-96a3-4a4d-95a2-c5a652fcb275.jpg?1783943688"
    }
}
