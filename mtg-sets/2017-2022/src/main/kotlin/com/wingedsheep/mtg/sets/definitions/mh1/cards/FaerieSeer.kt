package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Faerie Seer
 * {U}
 * Creature — Faerie Wizard
 * 1/1
 * Flying
 * When this creature enters, scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)
 */
val FaerieSeer = card("Faerie Seer") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Wizard"
    power = 1
    toughness = 1
    oracleText = "Flying\nWhen this creature enters, scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Scry(2)
        description = "When this creature enters, scry 2."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "51"
        artist = "Colin Boyer"
        flavorText = "\"The patterns of crossing ripples reveal the future to those who know how to read them.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1fcfeb4-1818-4e08-be4c-27b8a9dc12e6.jpg?1783933144"
    }
}
