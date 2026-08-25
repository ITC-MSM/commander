package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Yoked Plowbeast
 * {5}{W}{W}
 * Creature — Beast
 * 5 / 5
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * A vanilla body plus one parameterized keyword, so the whole script is a single
 * [KeywordAbility.cycling] entry — it carries the `{2}` cost itself and lowers to the
 * discard-and-draw ability from hand. Nothing goes on the card's `keywords` set: a cycling cost is
 * not a bare evergreen keyword.
 */
val YokedPlowbeast = card("Yoked Plowbeast") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 5
    oracleText = "Cycling {2} ({2}, Discard this card: Draw a card.)"

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31"
        artist = "Steve Argyle"
        flavorText = "\"It is sacrilege to confine a gargantuan to the grinding of straight lines. I will pray that it remembers who is the master of this land.\"\n—Syeena, elvish godtoucher"
        imageUri = "https://cards.scryfall.io/normal/front/d/d/ddbbc7dc-efdf-46e8-bf19-0daa4034f6ec.jpg"
    }
}
