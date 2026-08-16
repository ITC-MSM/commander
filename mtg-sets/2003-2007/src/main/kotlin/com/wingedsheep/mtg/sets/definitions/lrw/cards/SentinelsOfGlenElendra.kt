package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sentinels of Glen Elendra
 * {3}{U}
 * Creature — Faerie Soldier
 * 2/3
 * Flash
 * Flying
 */
val SentinelsOfGlenElendra = card("Sentinels of Glen Elendra") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Soldier"
    power = 2
    toughness = 3
    oracleText = "Flash\nFlying"

    keywords(Keyword.FLASH, Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "84"
        artist = "Howard Lyon"
        flavorText = "Some say the valley of Glen Elendra is mythical, and that rumors of its existence are nothing but a faerie prank. Others say it is the fae's most fiercely guarded secret."
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f48daf7e-2f8e-4179-a145-a6b36dd11d44.jpg?1783942897"
    }
}
