package com.wingedsheep.mtg.sets.definitions.bok.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Frost Ogre
 * {3}{R}{R}
 * Creature — Ogre Warrior
 * 5/3
 *
 * Vanilla — no rules text.
 */
val FrostOgre = card("Frost Ogre") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre Warrior"
    power = 5
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Dan Murayama Scott"
        flavorText = "Mountain ogres allowed blizzards to sheathe them in ice, both to reinforce their armor and to hide their pungent musk from potential prey."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a91e5f1-9179-4763-b7c9-b7ad5451f6d0.jpg?1783944191"
    }
}
