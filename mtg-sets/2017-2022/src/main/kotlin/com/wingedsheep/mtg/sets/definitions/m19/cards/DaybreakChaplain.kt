package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Daybreak Chaplain
 * {1}{W}
 * Creature — Human Cleric
 * 1/3
 * Lifelink (Damage dealt by this creature also causes you to gain that much life.)
 */
val DaybreakChaplain = card("Daybreak Chaplain") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 3
    oracleText = "Lifelink (Damage dealt by this creature also causes you to gain that much life.)"

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Volkan Baǵa"
        flavorText = "\"May the light shine through me to guide the lost.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2f461b1-c801-4f0c-8fd7-fe68b6078ac6.jpg?1783934608"
    }
}
