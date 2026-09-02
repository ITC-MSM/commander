package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Risen Sanctuary
 * {5}{G}{W}
 * Creature — Elemental
 * 8/8
 *
 * Vigilance
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * One evergreen keyword and nothing else.
 */
val RisenSanctuary = card("Risen Sanctuary") {
    manaCost = "{5}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Elemental"
    oracleText = "Vigilance"
    power = 8
    toughness = 8

    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "190"
        artist = "Chase Stone"
        flavorText = "When one of the great guardians arises, it sweeps enemies aside like chaff yet takes care not to crush a single insect underfoot."
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a0b6c136-2bbe-48c1-ac53-2a8221b96936.jpg?1783940334"
    }
}
