package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Elfhame Wurm
 * {4}{G}
 * Creature — Wurm
 * 5/4
 * Vigilance, trample
 */
val ElfhameWurm = card("Elfhame Wurm") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    oracleText = "Vigilance, trample"
    power = 5
    toughness = 4

    keywords(Keyword.VIGILANCE, Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "161"
        artist = "Victor Adame Minguez"
        flavorText = "The wurms consume everything in their path, so the elves make sure that their path goes through as many Phyrexians as possible."
        imageUri = "https://cards.scryfall.io/normal/front/e/f/ef2dc99b-083d-473e-b352-e8264353e85b.jpg?1783921301"
    }
}
