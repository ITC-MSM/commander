package com.wingedsheep.mtg.sets.definitions.vis.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Phyrexian Walker
 * {0}
 * Artifact Creature — Phyrexian Construct
 * 0/3
 *
 * Vanilla — no rules text.
 */
val PhyrexianWalker = card("Phyrexian Walker") {
    manaCost = "{0}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Phyrexian Construct"
    power = 0
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Bryan Talbot"
        flavorText = "\"I have heard terrible tales of black rains, ashen fields, and metal that screams. I have consoled myself that the tales were a myth of some fevered mind. But today I saw a walker—and now I fear the truth.\"\n—Kasib Ibn Naji, Letters"
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9f8a3979-2947-4692-8b2f-d4c07c534777.jpg?1783946973"
    }
}
