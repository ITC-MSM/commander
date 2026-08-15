package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Seagraf Skaab
 * {1}{U}
 * Creature — Zombie
 * 1/3
 *
 * Vanilla — no rules text.
 */
val SeagrafSkaab = card("Seagraf Skaab") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie"
    power = 1
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "84"
        artist = "Jama Jurabaev"
        flavorText = "\"Your recent work is an inspiration. I have the utmost respect for your approach, and your craft is impeccable. At your convenience, I would be honored to collaborate on a project.\"\n—Ludevic, letter to Geralf"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/065d497d-5cfd-43c9-8c86-9a1da3d7e17e.jpg?1783937788"
    }
}
