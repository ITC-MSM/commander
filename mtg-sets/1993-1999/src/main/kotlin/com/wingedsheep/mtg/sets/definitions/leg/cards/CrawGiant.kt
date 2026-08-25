package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Craw Giant
 * {3}{G}{G}{G}{G}
 * Creature — Giant
 * 6/4
 *
 * Trample
 * Rampage 2 (Whenever this creature becomes blocked, it gets +2/+2 until end of turn for each creature blocking it beyond the first.)
 *
 * Rampage is wired by the [card] builder's `rampage(n)` helper: the printed keyword
 * ability is display-only, and the +N/+N-per-extra-blocker behaviour lives in the
 * "becomes blocked" triggered ability the helper installs alongside it.
 */
val CrawGiant = card("Craw Giant") {
    manaCost = "{3}{G}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Giant"
    power = 6
    toughness = 4
    oracleText = "Trample\n" +
        "Rampage 2 (Whenever this creature becomes blocked, it gets +2/+2 until end of turn for each " +
        "creature blocking it beyond the first.)"

    keywords(Keyword.TRAMPLE)
    rampage(2)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "180"
        artist = "Christopher Rush"
        flavorText = "Harthag gave a jolly laugh as he surveyed the army before him. \"Ho ho ho! Midgets! You " +
            "think you can stand in my way?\""
        imageUri = "https://cards.scryfall.io/normal/front/7/0/707dadf0-735f-445d-9240-e49660913314.jpg?1783948049"
    }
}
