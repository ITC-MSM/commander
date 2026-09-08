package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.championCreature
import com.wingedsheep.sdk.model.Rarity

/**
 * Changeling Berserker
 * {3}{R}
 * Creature — Shapeshifter
 * 5/3
 *
 * Changeling
 * Haste
 * Champion a creature
 *
 * Champion (CR 702.72) is two linked triggered abilities, wired by the [championCreature] helper:
 * the enters half exiles another creature you control or sacrifices this, and the leaves half
 * returns the exiled card. Changeling makes the Berserker every creature type, but its champion
 * quality is the printed "a creature", not a tribe — so any creature you control is a legal choice.
 */
val ChangelingBerserker = card("Changeling Berserker") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Shapeshifter"
    power = 5
    toughness = 3
    oracleText = "Changeling (This card is every creature type.)\n" +
        "Haste\n" +
        "Champion a creature (When this enters, sacrifice it unless you exile another creature " +
        "you control. When this leaves the battlefield, that card returns to the battlefield.)"

    keywords(Keyword.CHANGELING, Keyword.HASTE)
    championCreature()

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "160"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9f2e8f24-2ee1-4516-bf60-7fe6a0c4baec.jpg?1783942877"
    }
}
