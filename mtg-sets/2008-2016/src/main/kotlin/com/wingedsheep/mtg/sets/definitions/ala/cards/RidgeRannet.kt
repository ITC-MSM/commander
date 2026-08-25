package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Ridge Rannet
 * {5}{R}{R}
 * Creature — Beast
 * 6 / 4
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * A vanilla body plus one keyword ability. [KeywordAbility.cycling] carries the whole printed
 * reminder text — the discard-and-draw activated ability that only works from hand — so no
 * `keywords(Keyword.CYCLING)` display flag is needed beside it.
 */
val RidgeRannet = card("Ridge Rannet") {
    manaCost = "{5}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Beast"
    power = 6
    toughness = 4
    oracleText = "Cycling {2} ({2}, Discard this card: Draw a card.)"

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "Jim Murray"
        flavorText = "\"Only those with the strength to seize their destiny deserve to have one.\"\n—Nacatl scratchforms"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/4275a8dd-f777-4160-b773-9a868e743218.jpg"
    }
}
