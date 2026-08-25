package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Impostor of the Sixth Pride
 * {1}{W}
 * Creature — Shapeshifter
 * 3/1
 * Changeling (This card is every creature type.)
 */
val ImpostorOfTheSixthPride = card("Impostor of the Sixth Pride") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Shapeshifter"
    power = 3
    toughness = 1
    oracleText = "Changeling (This card is every creature type.)"

    keywords(Keyword.CHANGELING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Chris Seaman"
        flavorText = "The tribe was as strong as his longing, their territory as vast as his isolation. The changeling knew he had found a home, and a form."
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83298c8a-02c4-4ada-9a41-4b973bb58ac6.jpg?1783933161"
    }
}
