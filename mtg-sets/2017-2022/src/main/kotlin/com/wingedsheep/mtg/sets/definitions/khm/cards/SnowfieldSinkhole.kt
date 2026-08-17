package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Snowfield Sinkhole
 *
 * Snow Land — Plains Swamp
 * ({T}: Add {W} or {B}.)
 * This land enters tapped.
 *
 * The whole card is one [EntersTapped] replacement effect: the parenthesised mana line is reminder
 * text for the intrinsic abilities the Plains and Swamp subtypes already grant, so writing a mana
 * ability here would double-tap the land. The Snow supertype rides along in the type line.
 */
val SnowfieldSinkhole = card("Snowfield Sinkhole") {
    manaCost = ""
    colorIdentity = "BW"
    typeLine = "Snow Land — Plains Swamp"
    oracleText = "({T}: Add {W} or {B}.)\n" +
        "This land enters tapped."

    // Mana abilities are intrinsic from the basic land types (Plains -> {W}, Swamp -> {B})

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "269"
        artist = "Marta Nael"
        flavorText = "\"Young Vitima bellowed to the Cosmos, daring the gods to punish her hubris! Well, she got their attention, as you can see.\"\n—Iskene, Kannah storyteller"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/6611dc5e-6acc-48df-b8c4-4b327314578b.jpg?1783928172"
    }
}
