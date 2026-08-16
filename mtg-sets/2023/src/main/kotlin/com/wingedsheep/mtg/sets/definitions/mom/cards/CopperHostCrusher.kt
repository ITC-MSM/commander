package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Copper Host Crusher
 * {6}{G}{G}
 * Creature — Phyrexian Bear Rhino
 * 8/8
 * Trample
 * Hexproof
 */
val CopperHostCrusher = card("Copper Host Crusher") {
    manaCost = "{6}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Phyrexian Bear Rhino"
    oracleText = "Trample\n" +
        "Hexproof (This creature can't be the target of spells or abilities your opponents control.)"
    power = 8
    toughness = 8

    keywords(Keyword.TRAMPLE, Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "181"
        artist = "Nicholas Gregory"
        flavorText = "Walls built to withstand assaults even from Ikoria's apex monsters were " +
            "quickly deemed tactically irrelevant by Vorinclex's forces."
        imageUri = "https://cards.scryfall.io/normal/front/a/f/af527344-9d88-4641-8f6d-0263a6797df3.jpg?1783916973"
    }
}
