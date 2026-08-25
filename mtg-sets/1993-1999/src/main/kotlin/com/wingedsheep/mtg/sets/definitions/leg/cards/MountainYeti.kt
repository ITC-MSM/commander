package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Mountain Yeti
 * {2}{R}{R}
 * Creature — Yeti
 * 3/3
 *
 * Mountainwalk (This creature can't be blocked as long as defending player controls a Mountain.)
 * Protection from white
 */
val MountainYeti = card("Mountain Yeti") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Yeti"
    power = 3
    toughness = 3
    oracleText = "Mountainwalk (This creature can't be blocked as long as defending player controls a Mountain.)\n" +
        "Protection from white"

    keywords(Keyword.MOUNTAINWALK)
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.WHITE)))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "159"
        artist = "Dan Frazier"
        flavorText = "The Yeti's single greatest asset is its unnerving ability to blend in with its surroundings."
        imageUri = "https://cards.scryfall.io/normal/front/0/9/09242f08-3bfc-4082-b32f-703c7fed62a0.jpg?1783948054"
    }
}
