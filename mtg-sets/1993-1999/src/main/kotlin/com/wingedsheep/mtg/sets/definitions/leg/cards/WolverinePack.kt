package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wolverine Pack
 * {2}{G}{G}
 * Creature — Wolverine
 * 2/4
 *
 * Rampage 2 (Whenever this creature becomes blocked, it gets +2/+2 until end of turn for each creature blocking it beyond the first.)
 *
 * Rampage is wired by the [card] builder's `rampage(n)` helper: the printed keyword
 * ability is display-only, and the +N/+N-per-extra-blocker behaviour lives in the
 * "becomes blocked" triggered ability the helper installs alongside it.
 */
val WolverinePack = card("Wolverine Pack") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolverine"
    power = 2
    toughness = 4
    oracleText = "Rampage 2 (Whenever this creature becomes blocked, it gets +2/+2 until end of turn for each " +
        "creature blocking it beyond the first.)"

    rampage(2)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "214"
        artist = "Jeff A. Menges"
        flavorText = "\"Give them great meals of beef and iron and steel, they will eat like wolves and fight " +
            "like devils.\" —William Shakespeare, *King Henry V*"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba5aee52-095e-4c69-93eb-5adac11ed1fc.jpg?1783948042"
    }
}
