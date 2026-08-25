package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ashcoat Bear
 * {1}{G}
 * Creature — Bear
 * 2/2
 * Flash (You may cast this spell any time you could cast an instant.)
 */
val AshcoatBear = card("Ashcoat Bear") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bear"
    power = 2
    toughness = 2
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)"

    keywords(Keyword.FLASH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "190"
        artist = "Carl Critchlow"
        flavorText = "The bears wade into time rifts to feed. They do not fear the storms, and they are quick enough to snatch prey just as it blinks in or out of time."
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b7a6ab5-8a8f-492c-8484-3089354ce8cf.jpg"
    }
}
