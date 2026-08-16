package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Pestilent Syphoner
 * {1}{B}
 * Creature — Phyrexian Insect
 * 1/1
 *
 * Flying
 * Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)
 */
val PestilentSyphoner = card("Pestilent Syphoner") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Insect"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)"

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Brian Valeza"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/947a1e44-3485-4e24-a34d-6c318584743c.jpg?1783918043"
    }
}
