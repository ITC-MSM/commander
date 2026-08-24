package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ledev Guardian
 * {3}{W}
 * Creature — Human Knight
 * 2/4
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 */
val LedevGuardian = card("Ledev Guardian") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)"
    power = 2
    toughness = 4

    keywords(Keyword.CONVOKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Kimonas Theodossiou"
        flavorText = "\"I've raised her since she was a pup, and she's raised me since I was a recruit.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cbeb4ae3-2a7a-44e1-923b-20c772fd1b8b.jpg?1783934198"
    }
}
