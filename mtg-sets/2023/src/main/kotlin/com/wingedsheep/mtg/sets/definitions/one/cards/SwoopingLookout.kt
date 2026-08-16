package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Swooping Lookout
 * {W}
 * Artifact Creature — Phyrexian Construct
 * 1/2
 *
 * Flying, vigilance
 */
val SwoopingLookout = card("Swooping Lookout") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Artifact Creature — Phyrexian Construct"
    power = 1
    toughness = 2
    oracleText = "Flying, vigilance"

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "35"
        artist = "Mike Franchina"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b62c740d-260d-4dfa-b6b3-9a1527538f89.jpg?1783918072"
    }
}
