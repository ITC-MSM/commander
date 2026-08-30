package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tome Scour
 * {U}
 * Sorcery
 * Target player mills five cards.
 *
 * Canonical printing: Magic 2010, the card's earliest printing. Reprinted in M14 as a `Printing`
 * row.
 *
 * [Patterns.Library.mill] is the gather-then-move pipeline; passing the bound target routes both
 * halves at the targeted player rather than the controller.
 */
val TomeScour = card("Tome Scour") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Target player mills five cards."

    spell {
        val player = target("target player", Targets.Player)
        effect = Patterns.Library.mill(5, player)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Steven Belledin"
        flavorText = "Genius is overrated, especially when it's someone else's."
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fdbdf96b-e7c5-42e5-9a16-03daafde40af.jpg"
    }
}
