package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cabal Evangel
 * {1}{B}
 * Creature — Human Cleric
 * 2/2
 */
val CabalEvangel = card("Cabal Evangel") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "78"
        artist = "Lius Lasahido"
        flavorText = "\"All hail the Demonlord Belzenlok, Evincar of the Stronghold, Scion of Darkness, Doom of Fools, Lord of the Wastes, Master of the Ebon Hand, Eternal Patriarch of the Cabal...\""
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d218d2a2-bb5d-4ea8-a131-341c574410b2.jpg?1562743442"
    }
}
