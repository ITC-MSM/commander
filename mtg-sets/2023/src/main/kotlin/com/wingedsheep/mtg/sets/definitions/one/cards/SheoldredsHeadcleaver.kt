package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Sheoldred's Headcleaver
 * {3}{B}
 * Creature — Phyrexian Warrior
 * 2/4
 *
 * Menace
 * Toxic 2 (Players dealt combat damage by this creature also get two poison counters.)
 */
val SheoldredsHeadcleaver = card("Sheoldred's Headcleaver") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Warrior"
    power = 2
    toughness = 4
    oracleText = "Menace\n" +
        "Toxic 2 (Players dealt combat damage by this creature also get two poison counters.)"

    keywords(Keyword.MENACE)
    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 2))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Andrey Kuzinskiy"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c6b818f-6912-4ea1-a35a-c8cd7ee9aead.jpg?1783918040"
    }
}
