package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Serra Sphinx
 * {3}{U}{U}
 * Creature — Sphinx
 * 4/4
 * Flying, vigilance
 */
val SerraSphinx = card("Serra Sphinx") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx"
    power = 4
    toughness = 4
    oracleText = "Flying, vigilance"

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "62"
        artist = "Daren Bader"
        flavorText = "Sphinxes drink from the mystic meres of Serra's realm, where their keen eyes watch reflections of what is and what is yet to come."
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9a4d7ac-082b-4f66-9b7f-4d1d0fda78c7.jpg"
    }
}
