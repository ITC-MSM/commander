package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Catacomb Slug
 * {4}{B}
 * Creature — Slug
 * 2/6
 *
 * Vanilla — no rules text.
 */
val CatacombSlug = card("Catacomb Slug") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Slug"
    power = 2
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "58"
        artist = "Nils Hamm"
        flavorText = "\"The entire murder scene was covered in dripping, oozing slime. No need for a soothsayer to solve that one.\"\n—Pel Javya, Wojek investigator"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53b36fba-6a0e-4f03-8bee-03919062537f.jpg?1783940365"
    }
}
