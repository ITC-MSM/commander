package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Territorial Maro
 * {4}{G}
 * Creature — Elemental
 * * / *
 * Domain — Territorial Maro's power and toughness are each equal to twice the number of basic land types among lands you control.
 */
val TerritorialMaro = card("Territorial Maro") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    oracleText = "Domain — Territorial Maro's power and toughness are each equal to twice the number of basic land types among lands you control."

    // Twice the domain count, in both halves of the printed `*`/`*`.
    dynamicPower(DynamicAmount.Multiply(DynamicAmounts.domain(), 2))
    dynamicToughness(DynamicAmount.Multiply(DynamicAmounts.domain(), 2))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "184"
        artist = "Simon Dominic"
        flavorText = "The Coalition was more than just treaties and alliances: it was the soul of a world rising up against the invading tide."
        imageUri = "https://cards.scryfall.io/normal/front/8/5/853ddf31-826b-411e-9b6d-75d53cdd1b84.jpg?1783921293"
    }
}
