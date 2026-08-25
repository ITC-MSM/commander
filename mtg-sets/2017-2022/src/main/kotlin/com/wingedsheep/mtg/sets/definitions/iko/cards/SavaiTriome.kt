package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Savai Triome
 * Land — Mountain Plains Swamp
 * ({T}: Add {R}, {W}, or {B}.)
 * This land enters tapped.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 *
 * The mana line is reminder text: the three basic land subtypes grant their mana abilities
 * intrinsically, so the script carries only the tapped-entry replacement and cycling.
 */
val SavaiTriome = card("Savai Triome") {
    colorIdentity = "BRW"
    typeLine = "Land — Mountain Plains Swamp"
    oracleText = "({T}: Add {R}, {W}, or {B}.)\nThis land enters tapped.\nCycling {3} ({3}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "253"
        artist = "Titus Lunter"
        flavorText = "Broad prairies feed the human sanctuary of Drannith and conceal a network of caverns where giant cats make their dens."
        imageUri = "https://cards.scryfall.io/normal/front/7/4/748e6a61-9c1f-4225-9f04-e54002f63ac3.jpg"
    }
}
