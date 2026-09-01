package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Archweaver
 * {5}{G}{G}
 * Creature — Spider
 * 5/5
 *
 * Reach, trample
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Two evergreen keywords and nothing else — the pair is `keywords(...)`, which is the whole script.
 */
val Archweaver = card("Archweaver") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    oracleText = "Reach, trample"
    power = 5
    toughness = 5

    keywords(Keyword.REACH, Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "114"
        artist = "Jason Felix"
        flavorText = "The silk of the archweavers adds structural integrity to otherwise unstable Izzet building sites."
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f99dc8ff-932c-4d56-9253-99ce9e145306.jpg?1783940352"
    }
}
