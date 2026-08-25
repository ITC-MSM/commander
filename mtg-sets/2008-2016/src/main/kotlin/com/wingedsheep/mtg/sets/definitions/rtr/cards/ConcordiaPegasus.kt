package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Concordia Pegasus — Return to Ravnica #8
 * {1}{W} · Creature — Pegasus · 1 / 3
 *
 * A vanilla flier. The canonical lives here, in Return to Ravnica — its earliest real
 * printing — and the later sets (RNA, M20, M21) carry Printing rows.
 */
val ConcordiaPegasus = card("Concordia Pegasus") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Pegasus"
    power = 1
    toughness = 3
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Winona Nelson"
        flavorText = "\"A kick from its hooves is like a bolt of lightning. I'd know. I've been hit by both.\"\n" +
        "—Rencz, Izzet chemister's aide"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0333d0b-ae42-48aa-83d8-a4f2c7483a46.jpg"
    }
}
