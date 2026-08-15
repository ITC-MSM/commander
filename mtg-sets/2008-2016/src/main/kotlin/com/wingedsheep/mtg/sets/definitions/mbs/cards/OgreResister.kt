package com.wingedsheep.mtg.sets.definitions.mbs.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ogre Resister
 * {2}{R}{R}
 * Creature — Ogre
 * 4/3
 *
 * Vanilla — no rules text.
 */
val OgreResister = card("Ogre Resister") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre"
    power = 4
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Efrem Palacios"
        flavorText = "He didn't have a word for \"home,\" but he knew it was something to be defended."
        imageUri = "https://cards.scryfall.io/normal/front/6/0/60b7407d-f677-403b-893c-361df456009a.jpg?1783941377"
    }
}
