package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hunding Gjornersen
 * {3}{W}{U}{U}
 * Legendary Creature — Human Warrior
 * 5/4
 *
 * Rampage 1 (Whenever this creature becomes blocked, it gets +1/+1 until end of turn for each creature blocking it beyond the first.)
 *
 * Rampage is wired by the [card] builder's `rampage(n)` helper: the printed keyword
 * ability is display-only, and the +N/+N-per-extra-blocker behaviour lives in the
 * "becomes blocked" triggered ability the helper installs alongside it.
 */
val HundingGjornersen = card("Hunding Gjornersen") {
    manaCost = "{3}{W}{U}{U}"
    colorIdentity = "UW"
    typeLine = "Legendary Creature — Human Warrior"
    power = 5
    toughness = 4
    oracleText = "Rampage 1 (Whenever this creature becomes blocked, it gets +1/+1 until end of turn for each " +
        "creature blocking it beyond the first.)"

    rampage(1)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "231"
        artist = "Richard Thomas"
        flavorText = "\"You would never guess, at the terrifying sight of the man, that Hunding was as charming a " +
            "companion as one could wish for.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/7/07d8e501-6857-4a52-a3b9-2bf0bee5b08c.jpg?1783948038"
    }
}
