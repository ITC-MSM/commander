package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kasimir the Lone Wolf
 * {4}{W}{U}
 * Legendary Creature — Human Warrior
 * 5/3
 *
 * Vanilla — no rules text.
 */
val KasimirTheLoneWolf = card("Kasimir the Lone Wolf") {
    manaCost = "{4}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Warrior"
    power = 5
    toughness = 3

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "237"
        artist = "Richard Kane Ferguson"
        flavorText = "Popular indeed is the tale of how Kasimir was once a Holy man who renounced his order to take up the sword. But this tale is no more likely than any other."
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45b1e60d-54dd-41cd-b9a2-00890725a3df.jpg?1783948037"
    }
}
