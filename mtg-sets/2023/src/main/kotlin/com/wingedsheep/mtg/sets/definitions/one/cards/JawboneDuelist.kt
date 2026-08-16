package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Jawbone Duelist
 * {1}{W}
 * Creature — Phyrexian Soldier
 * 1/1
 *
 * Double strike
 * Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)
 */
val JawboneDuelist = card("Jawbone Duelist") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Phyrexian Soldier"
    power = 1
    toughness = 1
    oracleText = "Double strike\n" +
        "Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)"

    keywords(Keyword.DOUBLE_STRIKE)
    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 1))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "18"
        artist = "Nino Vecia"
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b00ea7bb-705b-4669-bb2a-560bed04b14a.jpg?1783918078"
    }
}
