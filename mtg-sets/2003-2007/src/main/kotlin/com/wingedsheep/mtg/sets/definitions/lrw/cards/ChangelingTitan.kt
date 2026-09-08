package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.championCreature
import com.wingedsheep.sdk.model.Rarity

/**
 * Changeling Titan
 * {4}{G}
 * Creature — Shapeshifter
 * 7/7
 *
 * Changeling
 * Champion a creature
 */
val ChangelingTitan = card("Changeling Titan") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Shapeshifter"
    power = 7
    toughness = 7
    oracleText = "Changeling (This card is every creature type.)\n" +
        "Champion a creature (When this enters, sacrifice it unless you exile another creature " +
        "you control. When this leaves the battlefield, that card returns to the battlefield.)"

    keywords(Keyword.CHANGELING)
    championCreature()

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "200"
        artist = "Jesper Ejsing"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d5b9719-2861-477e-bb78-225fd03d7bbc.jpg?1783942866"
    }
}
