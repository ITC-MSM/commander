package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Arboretum Elemental
 * {7}{G}{G}
 * Creature — Elemental
 * 7/5
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Hexproof (This creature can't be the target of spells or abilities your opponents control.)
 */
val ArboretumElemental = card("Arboretum Elemental") {
    manaCost = "{7}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Hexproof (This creature can't be the target of spells or abilities your opponents control.)"
    power = 7
    toughness = 5

    keywords(Keyword.CONVOKE, Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "122"
        artist = "James Paick"
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f4400bf-134b-4011-985d-eed4e5ba1de8.jpg?1783934157"
    }
}
