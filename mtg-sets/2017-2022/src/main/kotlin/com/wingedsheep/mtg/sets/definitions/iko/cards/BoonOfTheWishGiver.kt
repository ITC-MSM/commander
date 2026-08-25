package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Boon of the Wish-Giver
 * {4}{U}{U}
 * Sorcery
 *
 * Draw four cards.
 * Cycling {1} ({1}, Discard this card: Draw a card.)
 */
val BoonOfTheWishGiver = card("Boon of the Wish-Giver") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Draw four cards.\nCycling {1} ({1}, Discard this card: Draw a card.)"

    spell {
        effect = Effects.DrawCards(4)
    }

    keywordAbility(KeywordAbility.cycling("{1}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "43"
        artist = "Chris Rahn"
        flavorText = "\"There is no law of nature that I have not seen Illuna break. Makes you wonder what is truly possible, does it not?\"\n—Rielle, the Everwise"
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0e790851-f0f7-4f1a-80e6-94be649499b6.jpg"
    }
}
