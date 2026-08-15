package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Slavering Branchsnapper
 * {4}{G}{G}
 * Creature — Lizard
 * 7/6
 * Trample
 * Forestcycling {2} ({2}, Discard this card: Search your library for a Forest card,
 * reveal it, put it into your hand, then shuffle.)
 */
val SlaveringBranchsnapper = card("Slavering Branchsnapper") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Lizard"
    power = 7
    toughness = 6
    oracleText = "Trample\nForestcycling {2}"

    keywords(Keyword.TRAMPLE)
    keywordAbility(KeywordAbility.typecycling("Forest", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "198"
        artist = "John Tedrick"
        flavorText = "\"Hey, look! It made us a nice big path through the hedge maze!\"\n—Zimone"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5ed7ca4d-5895-4074-8315-656363d14862.jpg?1726286606"
    }
}
