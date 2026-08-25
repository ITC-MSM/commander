package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Righteous Avengers
 * {4}{W}
 * Creature — Human Soldier
 * 3/1
 *
 * Plainswalk (This creature can't be blocked as long as defending player controls a Plains.)
 */
val RighteousAvengers = card("Righteous Avengers") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 1
    oracleText = "Plainswalk (This creature can't be blocked as long as defending player controls a Plains.)"

    keywords(Keyword.PLAINSWALK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "34"
        artist = "Heather Hudson"
        flavorText = "Few can withstand the wrath of the righteous."
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d96b463e-9579-4e7b-87c2-342527b91e7c.jpg?1783948081"
    }
}
