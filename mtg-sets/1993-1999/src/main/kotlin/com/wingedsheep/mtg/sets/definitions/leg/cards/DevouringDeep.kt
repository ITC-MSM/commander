package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Devouring Deep
 * {2}{U}
 * Creature — Fish
 * 1/2
 *
 * Islandwalk (This creature can't be blocked as long as defending player controls an Island.)
 */
val DevouringDeep = card("Devouring Deep") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Fish"
    power = 1
    toughness = 2
    oracleText = "Islandwalk (This creature can't be blocked as long as defending player controls an Island.)"

    keywords(Keyword.ISLANDWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Liz Danforth"
        flavorText = "\"Full fathom five thy father lies;/ Of his bones are coral made;/ Those are pearls that " +
            "were his eyes;/ Nothing of him that doth fade,/ But doth suffer a sea-change/ Into " +
            "something rich and strange.\" —William Shakespeare, *The Tempest*"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/0855a5a8-8c40-4396-9ad1-8fa0fc6a0c59.jpg?1783948077"
    }
}
