package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Testament Bearer
 * {3}{B}
 * Creature — Phyrexian Warrior
 * 4/1
 *
 * When this creature dies, look at the top three cards of your library. Put one of them into your hand and the rest into your graveyard.
 */
val TestamentBearer = card("Testament Bearer") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Warrior"
    power = 4
    toughness = 1
    oracleText = "When this creature dies, look at the top three cards of your library. Put one of them into your hand and the rest into your graveyard."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Patterns.Library.lookAtTopAndKeep(
            count = 3,
            keepCount = 1
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "Raluca Marinescu"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5424859-628a-4c19-acd0-0c63c21c8338.jpg?1783918039"
    }
}
