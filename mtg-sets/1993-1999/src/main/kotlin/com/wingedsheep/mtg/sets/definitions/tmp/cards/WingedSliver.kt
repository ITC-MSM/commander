package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Winged Sliver
 * {1}{U}
 * Creature — Sliver
 * 1/1
 * All Sliver creatures have flying.
 */
val WingedSliver = card("Winged Sliver") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Sliver creatures have flying."

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING, GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver")))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "106"
        artist = "Anthony S. Waters"
        flavorText = "\"Everything around here has cut a deal with gravity.\"\n" +
            "—Gerrard of the *Weatherlight*"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03aa58b4-dbc2-414e-aa7a-f09360d59b3c.jpg"
    }
}
