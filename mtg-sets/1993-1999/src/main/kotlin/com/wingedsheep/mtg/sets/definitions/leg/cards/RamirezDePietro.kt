package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ramirez DePietro
 * {3}{U}{B}{B}
 * Legendary Creature — Human Pirate
 * 4/3
 *
 * First strike
 */
val RamirezDePietro = card("Ramirez DePietro") {
    manaCost = "{3}{U}{B}{B}"
    colorIdentity = "BU"
    typeLine = "Legendary Creature — Human Pirate"
    power = 4
    toughness = 3
    oracleText = "First strike"

    keywords(Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "251"
        artist = "Phil Foglio"
        flavorText = "Ramirez DePietro is a most flamboyant pirate. Be careful not to believe his tall tales, " +
            "especially when you ask his age."
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e5c66c61-aadf-433b-9958-fc9b44b327b9.jpg?1783948034"
    }
}
