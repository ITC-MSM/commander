package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Doom Whisperer
 * {3}{B}{B}
 * Creature — Nightmare Demon
 * 6/6
 * Flying, trample
 * Pay 2 life: Surveil 2. (Look at the top two cards of your library, then put any number of them into your graveyard and the rest on top of your library in any order.)
 */
val DoomWhisperer = card("Doom Whisperer") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightmare Demon"
    oracleText = "Flying, trample\n" +
        "Pay 2 life: Surveil 2. (Look at the top two cards of your library, then put any number of them into your graveyard and the rest on top of your library in any order.)"
    power = 6
    toughness = 6

    keywords(Keyword.FLYING, Keyword.TRAMPLE)
    activatedAbility {
        cost = Costs.PayLife(2)
        effect = Patterns.Library.surveil(2)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "69"
        artist = "Vincent Proce"
        flavorText = "The sound of every twisted secret tempts you to hear another."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a11ee0d-ff8d-4648-8b4e-29440c135c30.jpg?1783934176"
    }
}
