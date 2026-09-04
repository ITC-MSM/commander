package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity

/**
 * Twins of Maurer Estate (Shadows over Innistrad #142)
 * {4}{B}
 * Creature — Vampire
 * 3 / 5
 *
 * Madness {2}{B} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)
 */
val TwinsOfMaurerEstate = card("Twins of Maurer Estate") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 3
    toughness = 5
    oracleText = "Madness {2}{B} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)"

    madness("{2}{B}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "Darek Zabrocki"
        flavorText = "\"Children, where are your parents?\"\n—Reig, wandering monk, last words"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3cc13a00-bd58-44fa-93af-846001ca4f84.jpg?1783937761"
    }
}
