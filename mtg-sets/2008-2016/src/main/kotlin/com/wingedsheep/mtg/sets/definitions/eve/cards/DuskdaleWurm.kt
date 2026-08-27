package com.wingedsheep.mtg.sets.definitions.eve.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Duskdale Wurm
 * {5}{G}{G}
 * Creature — Wurm
 * 7/7
 *
 * Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)
 *
 * A vanilla trampler. The canonical lives here, in Eventide — its earliest real printing —
 * and the later sets carry Printing rows.
 */
val DuskdaleWurm = card("Duskdale Wurm") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 7
    toughness = 7
    oracleText = "Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "67"
        artist = "Dan Dos Santos"
        flavorText = "\"Last time, it tore up the Wilt-Leaf, turned Mistmeadow into a mudhole, and made the river jump its banks. On the bright side, we were eating venison for weeks.\"\n" +
            "—Donal Alloway, cenn of Kinscaer"
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d10736d-047b-423f-9017-f59732d446bf.jpg?1783942679"
    }
}
