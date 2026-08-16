package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Bilious Skulldweller
 * {B}
 * Creature — Phyrexian Insect
 * 1/1
 *
 * Deathtouch
 * Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)
 */
val BiliousSkulldweller = card("Bilious Skulldweller") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Insect"
    power = 1
    toughness = 1
    oracleText = "Deathtouch\n" +
        "Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)"

    keywords(Keyword.DEATHTOUCH)
    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 1))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "83"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/d/f/dfb81cb1-ac56-4803-a962-359854a447df.jpg?1783918051"
    }
}
