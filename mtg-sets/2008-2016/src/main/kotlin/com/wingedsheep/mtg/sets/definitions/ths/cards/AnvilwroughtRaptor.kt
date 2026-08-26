package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Anvilwrought Raptor
 * {4}
 * Artifact Creature — Bird
 * 2 / 1
 *
 * Flying
 * First strike (This creature deals combat damage before creatures without first strike.)
 */
val AnvilwroughtRaptor = card("Anvilwrought Raptor") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Bird"
    power = 2
    toughness = 1
    oracleText = "Flying\nFirst strike (This creature deals combat damage before creatures without first strike.)"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "211"
        artist = "James Zapata"
        flavorText = "\"I know its lightness, for I have seen it fly. I know its weight, for I have seen it strike.\"\n—Brigone, soldier of Meletis"
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76330494-ce39-444e-b5da-8905bcccb8ad.jpg"
    }
}
