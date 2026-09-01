package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rubbleback Rhino
 * {4}{G}
 * Creature — Rhino
 * 3/4
 *
 * Hexproof (This creature can't be the target of spells or abilities your opponents control.)
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * One evergreen keyword and nothing else.
 */
val RubblebackRhino = card("Rubbleback Rhino") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Rhino"
    oracleText = "Hexproof (This creature can't be the target of spells or abilities your opponents control.)"
    power = 3
    toughness = 4

    keywords(Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "132"
        artist = "Johann Bodin"
        flavorText = "The trouble started when a street urchin bet a goblin he could ride one until the clock on Shilbo's Tower struck thirteen."
        imageUri = "https://cards.scryfall.io/normal/front/5/1/51daaf9b-d8a8-49a6-94e1-0c8be2c6188b.jpg?1783940347"
    }
}
