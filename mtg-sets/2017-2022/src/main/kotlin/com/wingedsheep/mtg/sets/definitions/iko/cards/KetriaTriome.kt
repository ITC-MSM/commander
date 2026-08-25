package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Ketria Triome
 * Land — Forest Island Mountain
 * ({T}: Add {G}, {U}, or {R}.)
 * This land enters tapped.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 *
 * The reminder-text mana ability is intrinsic to the basic land subtypes on the type line, so the
 * only scripted parts are the tapped entry and the cycling cost.
 */
val KetriaTriome = card("Ketria Triome") {
    colorIdentity = "GRU"
    typeLine = "Land — Forest Island Mountain"
    oracleText = "({T}: Add {G}, {U}, or {R}.)\nThis land enters tapped.\nCycling {3} ({3}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "250"
        artist = "Sam Burley"
        flavorText = "Nowhere on Ikoria are monsters more integral to the landscape than Ketria, where the river itself will stand up and roar."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a249b1f4-2b22-4b67-a207-e0c4ae95d2e1.jpg"
    }
}
