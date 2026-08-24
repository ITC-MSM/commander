package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Snare Thopter
 * {4}
 * Artifact Creature — Thopter
 * 3/2
 *
 * Flying, haste
 *
 * Two printed keywords and nothing else.
 */
val SnareThopter = card("Snare Thopter") {
    manaCost = "{4}"
    typeLine = "Artifact Creature — Thopter"
    oracleText = "Flying, haste"
    power = 3
    toughness = 2

    keywords(Keyword.FLYING, Keyword.HASTE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "236"
        artist = "John Avon"
        flavorText = "\"I did not think I would understand the cold filigree creatures of this world, but they too are governed by the laws of nature.\"\n—Nissa Revane"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/687febd3-1825-4fd2-b9ab-bd32a9baffc7.jpg?1783937146"
    }
}
