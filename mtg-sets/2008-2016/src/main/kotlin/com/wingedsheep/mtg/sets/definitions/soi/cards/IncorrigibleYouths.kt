package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity

/**
 * Incorrigible Youths (Shadows over Innistrad #166)
 * {3}{R}{R}
 * Creature — Vampire
 * 4 / 3
 *
 * Haste (This creature can attack and {T} as soon as it comes under your control.)
 * Madness {2}{R} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)
 *
 * Madness (CR 702.35) on a creature is what makes the haste matter: the card is cast for {2}{R}
 * while the madness trigger resolves, so a discard outlet turns a five-drop into a three-mana
 * 4/3 that can attack the turn it lands.
 */
val IncorrigibleYouths = card("Incorrigible Youths") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    power = 4
    toughness = 3
    oracleText = "Haste (This creature can attack and {T} as soon as it comes under your control.)\n" +
        "Madness {2}{R} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)"

    keywords(Keyword.HASTE)

    madness("{2}{R}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "166"
        artist = "Winona Nelson"
        flavorText = "\"Ah, to be young again.\"\n—Olivia Voldaren"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4e826571-6f00-4b40-975b-d51b2b17b4a4.jpg?1783937749"
    }
}
