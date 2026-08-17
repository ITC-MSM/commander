package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Highland Forest
 *
 * Snow Land — Mountain Forest
 * ({T}: Add {R} or {G}.)
 * This land enters tapped.
 *
 * The whole card is one [EntersTapped] replacement effect: the parenthesised mana line is reminder
 * text for the intrinsic abilities the Mountain and Forest subtypes already grant, so writing a mana
 * ability here would double-tap the land. The Snow supertype rides along in the type line.
 */
val HighlandForest = card("Highland Forest") {
    manaCost = ""
    colorIdentity = "GR"
    typeLine = "Snow Land — Mountain Forest"
    oracleText = "({T}: Add {R} or {G}.)\n" +
        "This land enters tapped."

    // Mana abilities are intrinsic from the basic land types (Mountain -> {R}, Forest -> {G})

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "261"
        artist = "Alayna Danner"
        flavorText = "\"Tread carefully! The last time I walked this path, half the snowdrifts were, in fact, sleeping trolls. I've never run so fast in my life!\"\n—Iskene, Kannah storyteller"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/682eee5f-7986-45d3-910f-407303fdbcc4.jpg?1783928176"
    }
}
