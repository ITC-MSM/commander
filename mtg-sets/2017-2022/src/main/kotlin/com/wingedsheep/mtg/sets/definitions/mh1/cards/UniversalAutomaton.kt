package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Universal Automaton
 * {1}
 * Artifact Creature — Shapeshifter
 * 1/1
 * Changeling (This card is every creature type.)
 */
val UniversalAutomaton = card("Universal Automaton") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Shapeshifter"
    power = 1
    toughness = 1
    oracleText = "Changeling (This card is every creature type.)"

    keywords(Keyword.CHANGELING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "235"
        artist = "Ben Maier"
        flavorText = "\"Within minutes, the strange device was indistinguishable from the other upon my workbench.\" —Tocasia, journal entry"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53c682e2-c90f-4f4b-9010-00b099e85518.jpg?1783933069"
    }
}
