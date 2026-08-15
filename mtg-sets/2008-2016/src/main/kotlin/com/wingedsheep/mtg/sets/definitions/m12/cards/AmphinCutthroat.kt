package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Amphin Cutthroat
 * {3}{U}
 * Creature — Salamander Rogue
 * 2/4
 *
 * Vanilla — no rules text.
 */
val AmphinCutthroat = card("Amphin Cutthroat") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Salamander Rogue"
    power = 2
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Howard Lyon"
        flavorText = "\"The amphin have long built their society in secret. While surface dwellers squabbled over trivial borders, they patiently expanded, building their ammonite temple-caves. Now amphin priests eye the shore, and amphin hunters gird for war.\"\n—Gor Muldrak, *Cryptohistories*"
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd169064-9c7b-40bd-8be0-a89fcb28ae2f.jpg?1783941096"
    }
}
