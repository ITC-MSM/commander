package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Brine Giant
 * {6}{U}
 * Creature — Giant
 * 5/6
 *
 * Affinity for enchantments (This spell costs {1} less to cast for each enchantment you control.)
 *
 * The stock [KeywordAbility.Affinity] cost reduction, here over [CardType.ENCHANTMENT] rather than
 * the Mirrodin-era artifacts: `CostCalculator` counts permanents of the *declared* `forType`, so no
 * new vocabulary is needed for Theros' enchantment-matters spin on the keyword.
 *
 * Affinity is a cost reduction, not an alternative cost — Brine Giant's mana value stays 7 in every
 * zone however cheaply it was cast, and only its generic {6} can be shaved.
 */
val BrineGiant = card("Brine Giant") {
    manaCost = "{6}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Giant"
    power = 5
    toughness = 6
    oracleText = "Affinity for enchantments (This spell costs {1} less to cast for each enchantment you control.)"

    keywordAbility(KeywordAbility.Affinity(CardType.ENCHANTMENT))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Yongjae Choi"
        flavorText = "The oracles of Meletis foresaw neither its rise from the depths nor the destruction " +
            "it would leave in its wake."
        imageUri = "https://cards.scryfall.io/normal/front/6/8/6811a9dc-e521-4c9e-accb-1efb8346c1db.jpg"
    }
}
