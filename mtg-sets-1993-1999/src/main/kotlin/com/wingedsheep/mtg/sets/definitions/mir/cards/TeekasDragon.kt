package com.wingedsheep.mtg.sets.definitions.mir.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Teeka's Dragon
 * {9}
 * Artifact Creature — Dragon
 * 5/5
 *
 * Flying; trample; rampage 4 (Whenever this creature becomes blocked, it gets +4/+4
 * until end of turn for each creature blocking it beyond the first.)
 *
 * Rampage (CR 702.23) is wired by the [card] builder's `rampage(n)` helper: the keyword
 * ability is display-only and the +N/+N-per-extra-blocker behavior lives in a
 * "becomes blocked" triggered ability.
 */
val TeekasDragon = card("Teeka's Dragon") {
    manaCost = "{9}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Dragon"
    power = 5
    toughness = 5
    oracleText = "Flying; trample; rampage 4 (Whenever this creature becomes blocked, it gets " +
        "+4/+4 until end of turn for each creature blocking it beyond the first.)"

    keywords(Keyword.FLYING, Keyword.TRAMPLE)
    rampage(4)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "320"
        artist = "Liz Danforth"
        imageUri = "https://cards.scryfall.io/normal/front/5/7/57e8971d-baeb-4e4f-8c4d-0e8109e4505e.jpg?1562719278"
    }
}
