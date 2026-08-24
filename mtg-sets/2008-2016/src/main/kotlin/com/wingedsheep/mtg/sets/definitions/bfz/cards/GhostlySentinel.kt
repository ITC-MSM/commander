package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ghostly Sentinel
 * {4}{W}
 * Creature — Kor Spirit
 * 3/3
 * Flying, vigilance
 */
val GhostlySentinel = card("Ghostly Sentinel") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Spirit"
    power = 3
    toughness = 3
    oracleText = "Flying, vigilance"

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Daarken"
        flavorText = "Mystics of the Stone Havens call upon the spirits of fallen heroes to defend the refuges."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de867066-df5c-4412-9d51-56626b6d0220.jpg?1783938220"
    }
}
