package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Indatha Triome
 * Land — Plains Swamp Forest
 * ({T}: Add {W}, {B}, or {G}.)
 * This land enters tapped.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 *
 * The reminder-text mana ability is intrinsic to the basic land subtypes on the type line, so the
 * only scripted parts are the tapped entry and the cycling cost.
 */
val IndathaTriome = card("Indatha Triome") {
    colorIdentity = "BGW"
    typeLine = "Land — Plains Swamp Forest"
    oracleText = "({T}: Add {W}, {B}, or {G}.)\nThis land enters tapped.\nCycling {3} ({3}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "248"
        artist = "Noah Bradley"
        flavorText = "\"These lowlands were formed thousands of years ago by the behemoth Indath—its final footsteps before vanishing into the sea.\"\n—*Tales of the Ozolith*"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b74bb81-fb9a-40e5-a941-e517430b52f5.jpg"
    }
}
