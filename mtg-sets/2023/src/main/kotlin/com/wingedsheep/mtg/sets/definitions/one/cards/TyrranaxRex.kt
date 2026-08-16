package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Tyrranax Rex
 * {4}{G}{G}{G}
 * Creature — Phyrexian Dinosaur
 * 8/8
 *
 * This spell can't be countered.
 * Trample, ward {4}, haste
 * Toxic 4 (Players dealt combat damage by this creature also get four poison counters.)
 *
 * "This spell can't be countered" is a property of the spell on the stack, not a static
 * ability of the permanent — it is the card-builder's [cantBeCountered] flag (Pearl Lake
 * Ancient's shape).
 */
val TyrranaxRex = card("Tyrranax Rex") {
    manaCost = "{4}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Phyrexian Dinosaur"
    power = 8
    toughness = 8
    oracleText = "This spell can't be countered.\n" +
        "Trample, ward {4}, haste\n" +
        "Toxic 4 (Players dealt combat damage by this creature also get four poison counters.)"

    cantBeCountered = true

    keywords(Keyword.TRAMPLE, Keyword.HASTE)
    keywordAbility(KeywordAbility.ward("{4}"))
    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 4))

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "189"
        artist = "Tuan Duong Chu"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fb52b44-da5f-4f7a-a6c2-7924b855e051.jpg?1783918007"
    }
}
