package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Yotian Medic
 * {2}{W}
 * Creature — Human Cleric Soldier
 * 1/4
 * Lifelink
 */
val YotianMedic = card("Yotian Medic") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric Soldier"
    power = 1
    toughness = 4
    oracleText = "Lifelink"

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Aaron J. Riley"
        flavorText = "\"Go, save as many as you can! The city walls may be rubble, but Kroog will live on in its people.\"\n—Queen Kayla bin-Kroog"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f21b9c48-6eca-4677-961b-614f5ec594ce.jpg?1783920120"
    }
}
