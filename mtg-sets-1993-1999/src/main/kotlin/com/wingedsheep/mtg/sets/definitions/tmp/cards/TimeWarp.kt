package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.TakeExtraTurnEffect

/**
 * Time Warp
 * {3}{U}{U}
 * Sorcery
 * Target player takes an extra turn after this one.
 */
val TimeWarp = card("Time Warp") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Target player takes an extra turn after this one."

    spell {
        val player = target("player", Targets.Player)
        effect = TakeExtraTurnEffect(target = player)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "97"
        artist = "Pete Venters"
        imageUri = "https://cards.scryfall.io/normal/front/3/4/3447aeaf-3b26-442a-99d4-0a7ee76c8e76.jpg?1562053291"
    }
}
