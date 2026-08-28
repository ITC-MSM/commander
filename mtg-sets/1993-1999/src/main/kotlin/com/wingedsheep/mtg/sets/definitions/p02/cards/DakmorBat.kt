package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dakmor Bat
 * {1}{B}
 * Creature — Bat
 * 1/1
 * Flying
 */
val DakmorBat = card("Dakmor Bat") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Bat"
    oracleText = "Flying"
    power = 1
    toughness = 1
    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Una Fricker"
        flavorText = "The bat thrives on what underestimates it."
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f45994db-776d-420e-9241-99bf3b71fa59.jpg"
    }
}
