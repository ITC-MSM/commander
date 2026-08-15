package com.wingedsheep.mtg.sets.definitions.usg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Disciple of Law
 * {1}{W}
 * Creature — Human Cleric
 * 1/2
 * Protection from red
 * Cycling {2}
 */
val DiscipleOfLaw = card("Disciple of Law") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 2
    oracleText = "Protection from red\nCycling {2}"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.RED)))
    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Matthew D. Wilson"
        flavorText = "A religious order for religious order."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a5c8701-a294-4474-9747-129f972cfb18.jpg?1562920710"
    }
}
