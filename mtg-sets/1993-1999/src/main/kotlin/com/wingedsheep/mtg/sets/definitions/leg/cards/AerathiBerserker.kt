package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Aerathi Berserker
 * {2}{R}{R}{R}
 * Creature — Human Berserker
 * 2/4
 *
 * Rampage 3 (Whenever this creature becomes blocked, it gets +3/+3 until end of turn for each creature blocking it beyond the first.)
 *
 * Rampage is wired by the [card] builder's `rampage(n)` helper: the printed keyword
 * ability is display-only, and the +N/+N-per-extra-blocker behaviour lives in the
 * "becomes blocked" triggered ability the helper installs alongside it.
 */
val AerathiBerserker = card("Aerathi Berserker") {
    manaCost = "{2}{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Berserker"
    power = 2
    toughness = 4
    oracleText = "Rampage 3 (Whenever this creature becomes blocked, it gets +3/+3 until end of turn for each " +
        "creature blocking it beyond the first.)"

    rampage(3)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "131"
        artist = "Melissa A. Benson"
        flavorText = "Ærathi children who show promise are left to survive for a year in the wilderness. Those " +
            "who return are shown the way of the Berserker."
        imageUri = "https://cards.scryfall.io/normal/front/0/6/06673800-22a7-4ee3-92fa-7c7cd4865d30.jpg?1783948059"
    }
}
