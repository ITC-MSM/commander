package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Child of Night
 * {1}{B}
 * Creature — Vampire
 * 2/1
 *
 * Lifelink
 */
val ChildOfNight = card("Child of Night") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    oracleText = "Lifelink"
    power = 2
    toughness = 1

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "Ash Wood"
        flavorText = "A vampire enacts vengeance on the entire world, claiming her debt two tiny pinpricks at a time."
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e1f7a9a7-3679-4a18-a52a-e3a8ab16ad32.jpg?1783942385"
    }
}
