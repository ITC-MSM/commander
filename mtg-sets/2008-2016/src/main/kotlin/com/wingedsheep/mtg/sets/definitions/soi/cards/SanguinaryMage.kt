package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sanguinary Mage (Shadows over Innistrad #178)
 * {1}{R}
 * Creature — Vampire Wizard
 * 1 / 3
 *
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 *
 * The reminder text stays in `oracleText`; the behaviour comes entirely from [Keyword.PROWESS],
 * which the engine reads directly.
 */
val SanguinaryMage = card("Sanguinary Mage") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire Wizard"
    power = 1
    toughness = 3
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)"

    keywords(Keyword.PROWESS)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "David Gaillet"
        flavorText = "New cults rising in Nephalia have found allies in the Stromkirk bloodline, whose progenitor worships ancient and terrible forces."
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ace86fac-769c-4440-9a40-318d7172555b.jpg?1783937744"
    }
}
