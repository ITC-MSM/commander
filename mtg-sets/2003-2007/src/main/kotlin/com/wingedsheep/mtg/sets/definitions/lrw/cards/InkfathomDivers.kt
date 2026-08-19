package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Inkfathom Divers
 * {3}{U}{U}
 * Creature — Merfolk Soldier
 * 3/3
 * Islandwalk (This creature can't be blocked as long as defending player controls an Island.)
 * When this creature enters, look at the top four cards of your library, then put them back in
 * any order.
 */
val InkfathomDivers = card("Inkfathom Divers") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Soldier"
    power = 3
    toughness = 3
    oracleText = "Islandwalk (This creature can't be blocked as long as defending player controls " +
        "an Island.)\nWhen this creature enters, look at the top four cards of your library, then " +
        "put them back in any order."

    keywords(Keyword.ISLANDWALK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopAndReorder(4)
        description = "look at the top four cards of your library, then put them back in any order."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Steven Belledin"
        flavorText = "\"None appreciate sun and shallows like those who have seen the depths.\"\n—Lianda of the Stonybrook school"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cce7ce6e-0d84-4d66-a1dd-26ddac73d47b.jpg?1783942901"
    }
}
