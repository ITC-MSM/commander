package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ringwarden Owl
 * {3}{U}{U}
 * Creature — Bird
 * 3/3
 *
 * Flying (This creature can't be blocked except by creatures with flying or reach.)
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 */
val RingwardenOwl = card("Ringwarden Owl") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
        "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)"
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)
    prowess()

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Titus Lunter"
        flavorText = "The owls learn of mana from the mages who know it best."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1acf216d-ef8f-431b-9b65-1e2e91285517.jpg"
    }
}
