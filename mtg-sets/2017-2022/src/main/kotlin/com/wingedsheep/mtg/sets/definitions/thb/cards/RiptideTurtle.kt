package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Riptide Turtle
 * {1}{U}
 * Creature — Turtle
 * 0/5
 *
 * Flash
 * Defender
 *
 * Two printed keywords on the keyword set — flash is a casting permission the engine reads off the
 * card, defender a combat restriction; neither needs a parameterized [com.wingedsheep.sdk.scripting.KeywordAbility].
 */
val RiptideTurtle = card("Riptide Turtle") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Turtle"
    power = 0
    toughness = 5
    oracleText = "Flash\n" +
        "Defender"

    keywords(Keyword.FLASH, Keyword.DEFENDER)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "61"
        artist = "Brian Valeza"
        flavorText = "\"As the storm waves crushed my sailors, I cried out to Thassa. The next time I saw them, " +
            "hard shells encased them, and they swam away to safety.\"\n—Siona, captain of the *Pyleas*"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc4ba296-950c-4e39-ab5e-06be07e4a190.jpg"
    }
}
