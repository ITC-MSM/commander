package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Octoprophet
 * {3}{U}
 * Creature — Octopus
 * 3/3
 * When this creature enters, scry 2. (Look at the top two cards of your library, then put any
 * number of them on the bottom and the rest on top in any order.)
 */
val Octoprophet = card("Octoprophet") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Octopus"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.scry(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Grzegorz Rutkowski"
        flavorText = "In every swirl of the tide, it sees the awakening of things yet to come."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13becea1-e745-4c96-bfc2-6a277fb60ee1.jpg?1783933006"
    }
}
