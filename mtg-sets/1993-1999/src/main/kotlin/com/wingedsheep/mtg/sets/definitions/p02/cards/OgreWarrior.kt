package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ogre Warrior
 * {3}{R}
 * Creature — Ogre Warrior
 * 3/3
 *
 * Vanilla — no rules text.
 */
val OgreWarrior = card("Ogre Warrior") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre Warrior"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "113"
        artist = "Jeff Miracola"
        flavorText = "Assault and battery included."
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66e72970-df1f-4ded-a686-036008555a76.jpg?1783946462"
    }
}
