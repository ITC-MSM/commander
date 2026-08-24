package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rosemane Centaur
 * {3}{G}{W}
 * Creature — Centaur Soldier
 * 4/4
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Vigilance
 */
val RosemaneCentaur = card("Rosemane Centaur") {
    manaCost = "{3}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Centaur Soldier"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Vigilance"
    power = 4
    toughness = 4

    keywords(Keyword.CONVOKE, Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "197"
        artist = "Nils Hamm"
        flavorText = "\"I long for peaceful times, when I may tend to my garden instead of our borders.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/e/cee30585-f7b1-4ca4-8171-dc5a837f2b93.jpg?1783934126"
    }
}
