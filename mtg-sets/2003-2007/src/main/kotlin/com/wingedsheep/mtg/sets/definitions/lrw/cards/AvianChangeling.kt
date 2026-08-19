package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Avian Changeling
 * {2}{W}
 * Creature — Shapeshifter
 * 2/2
 * Changeling (This card is every creature type.)
 * Flying
 */
val AvianChangeling = card("Avian Changeling") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Shapeshifter"
    power = 2
    toughness = 2
    oracleText = "Changeling (This card is every creature type.)\nFlying"

    keywords(Keyword.CHANGELING, Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Heather Hudson"
        flavorText = "Today it flies with the flock. Tomorrow it may wake to find them gone, its body in an unfamiliar form."
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2258bd3-e38b-4029-a9fb-f9ae86dbbc3a.jpg?1783942918"
    }
}
