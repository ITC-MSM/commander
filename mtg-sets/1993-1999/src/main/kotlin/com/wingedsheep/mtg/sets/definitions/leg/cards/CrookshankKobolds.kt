package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Crookshank Kobolds
 * {0}
 * Creature — Kobold
 * 0/1
 *
 * Vanilla — no rules text.
 */
val CrookshankKobolds = card("Crookshank Kobolds") {
    manaCost = "{0}"
    colorIdentity = "R"
    typeLine = "Creature — Kobold"
    power = 0
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "141"
        artist = "Christopher Rush"
        flavorText = "The Crookshank military boasts a standing army of nearly twenty-four million, give or take twenty-two million."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7af6b119-7db4-49dd-aaa4-044b8c133f13.jpg?1783948058"
    }
}
