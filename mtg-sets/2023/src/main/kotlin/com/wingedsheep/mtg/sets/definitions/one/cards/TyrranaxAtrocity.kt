package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Tyrranax Atrocity
 * {3}{G}{G}
 * Creature — Phyrexian Dinosaur
 * 4/4
 *
 * Haste
 * Toxic 3 (Players dealt combat damage by this creature also get three poison counters.)
 */
val TyrranaxAtrocity = card("Tyrranax Atrocity") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Phyrexian Dinosaur"
    power = 4
    toughness = 4
    oracleText = "Haste\n" +
        "Toxic 3 (Players dealt combat damage by this creature also get three poison counters.)"

    keywords(Keyword.HASTE)
    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 3))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "188"
        artist = "Xavier Ribeiro"
        imageUri = "https://cards.scryfall.io/normal/front/1/5/157cf43c-f7f2-4362-bfc8-11682e94b747.jpg?1783918007"
    }
}
