package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Timberland Ancient
 * {4}{G}{G}
 * Creature — Treefolk
 * 6/5
 * Reach, trample
 * Forestcycling {2}
 */
val TimberlandAncient = card("Timberland Ancient") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk"
    oracleText = "Reach, trample\n" +
        "Forestcycling {2} ({2}, Discard this card: Search your library for a Forest card, reveal " +
        "it, put it into your hand, then shuffle.)"
    power = 6
    toughness = 5

    keywords(Keyword.REACH, Keyword.TRAMPLE)
    keywordAbility(KeywordAbility.typecycling("Forest", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "210"
        artist = "Pavel Kolomeyets"
        flavorText = "Only the trees were old enough to remember what happened when Moag ignored " +
            "warnings of Phyrexia millennia ago. They were determined not to repeat the mistake."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f73cd60-cd89-4c7a-85a6-e0ae34ca101b.jpg?1783916960"
    }
}
