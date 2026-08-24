package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Plated Crusher
 * {4}{G}{G}{G}
 * Creature — Beast
 * 7/6
 * Trample
 * Hexproof (This creature can't be the target of spells or abilities your opponents control.)
 */
val PlatedCrusher = card("Plated Crusher") {
    manaCost = "{4}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 7
    toughness = 6
    oracleText = "Trample\n" +
        "Hexproof (This creature can't be the target of spells or abilities your opponents control.)"

    keywords(Keyword.TRAMPLE, Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "183"
        artist = "Jama Jurabaev"
        flavorText = "\"It might fight the Eldrazi, but don't mistake it for a companion, Gideon. It's not " +
            "interested in your concept of strength through unity.\"\n" +
            "—Najiya, leader of the Tajuru"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd68e01c-4a09-450b-bfa0-8fbac8721764.jpg?1783938186"
    }
}
