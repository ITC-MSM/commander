package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Raugrin Triome
 * Land — Island Mountain Plains
 * ({T}: Add {U}, {R}, or {W}.)
 * This land enters tapped.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 *
 * The mana line is printed in reminder-text parentheses because it isn't an ability the card
 * grants itself — it comes from the basic land types on the type line (CR 305.6), which the engine
 * reads intrinsically. So the only scripted parts are the tapped-entry replacement and cycling.
 */
val RaugrinTriome = card("Raugrin Triome") {
    colorIdentity = "RUW"
    typeLine = "Land — Island Mountain Plains"
    oracleText = "({T}: Add {U}, {R}, or {W}.)\nThis land enters tapped.\nCycling {3} ({3}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "251"
        artist = "Jonas De Ro"
        flavorText = "Raugrin meets the sea with jaws wide, its coast spiked with teeth of crystal and granite."
        imageUri = "https://cards.scryfall.io/normal/front/0/2/02138fbb-3962-4348-8d31-faaefba0b8b2.jpg"
    }
}
