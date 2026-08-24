package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Watcher in the Mist
 * {3}{U}{U}
 * Creature — Spirit
 * 3/4
 * Flying
 * When this creature enters, surveil 2. (Look at the top two cards of your library, then put any number of them into your graveyard and the rest on top of your library in any order.)
 */
val WatcherInTheMist = card("Watcher in the Mist") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    oracleText = "Flying\n" +
        "When this creature enters, surveil 2. (Look at the top two cards of your library, then put any number of them into your graveyard and the rest on top of your library in any order.)"
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.surveil(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "Ryan Yee"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb971f49-8898-444a-a17c-caeb1696c62a.jpg?1783934181"
    }
}
