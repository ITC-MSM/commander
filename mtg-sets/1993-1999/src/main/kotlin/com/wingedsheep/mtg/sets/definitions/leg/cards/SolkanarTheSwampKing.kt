package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Sol'kanar the Swamp King
 * {2}{U}{B}{R}
 * Legendary Creature — Demon
 * 5/5
 *
 * Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)
 * Whenever a player casts a black spell, you gain 1 life.
 */
val SolkanarTheSwampKing = card("Sol'kanar the Swamp King") {
    manaCost = "{2}{U}{B}{R}"
    colorIdentity = "BRU"
    typeLine = "Legendary Creature — Demon"
    power = 5
    toughness = 5
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)\n" +
        "Whenever a player casts a black spell, you gain 1 life."

    keywords(Keyword.SWAMPWALK)
    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Any.withColor(Color.BLACK))
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "259"
        artist = "Richard Kane Ferguson"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a20dcb0-5350-40e0-82d3-c8d0186fc9d2.jpg?1783948032"
    }
}
