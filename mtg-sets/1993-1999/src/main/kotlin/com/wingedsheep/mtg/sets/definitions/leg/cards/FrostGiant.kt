package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Frost Giant
 * {3}{R}{R}{R}
 * Creature — Giant
 * 4/4
 *
 * Rampage 2 (Whenever this creature becomes blocked, it gets +2/+2 until end of turn for each creature blocking it beyond the first.)
 *
 * Rampage is wired by the [card] builder's `rampage(n)` helper: the printed keyword
 * ability is display-only, and the +N/+N-per-extra-blocker behaviour lives in the
 * "becomes blocked" triggered ability the helper installs alongside it.
 */
val FrostGiant = card("Frost Giant") {
    manaCost = "{3}{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant"
    power = 4
    toughness = 4
    oracleText = "Rampage 2 (Whenever this creature becomes blocked, it gets +2/+2 until end of turn for each " +
        "creature blocking it beyond the first.)"

    rampage(2)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "148"
        artist = "Daniel Gelon"
        flavorText = "The Frost Giants have been out in the cold a long, long time, but they have their rage to " +
            "keep them warm."
        imageUri = "https://cards.scryfall.io/normal/front/6/9/6955d54f-7b37-4e43-8183-51677fb1ee11.jpg?1783948056"
    }
}
