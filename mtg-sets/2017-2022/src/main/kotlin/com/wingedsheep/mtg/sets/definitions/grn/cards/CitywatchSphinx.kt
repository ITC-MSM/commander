package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Citywatch Sphinx
 * {5}{U}
 * Creature — Sphinx
 * 5/4
 * Flying
 * When this creature dies, surveil 2. (Look at the top two cards of your library, then put any number of them into your graveyard and the rest on top of your library in any order.)
 */
val CitywatchSphinx = card("Citywatch Sphinx") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx"
    oracleText = "Flying\n" +
        "When this creature dies, surveil 2. (Look at the top two cards of your library, then put any number of them into your graveyard and the rest on top of your library in any order.)"
    power = 5
    toughness = 4

    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.Dies
        effect = Patterns.Library.surveil(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "33"
        artist = "Magali Villeneuve"
        flavorText = "All those who trade in questions must answer to the Dimir."
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b809f89-13c7-4236-86a5-60745defb271.jpg?1783934191"
    }
}
