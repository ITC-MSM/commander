package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Forest Bear
 * {1}{G}
 * Creature — Bear
 * 2/2
 *
 * Vanilla — no rules text.
 */
val ForestBear = card("Forest Bear") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bear"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Wang Yuqun"
        flavorText = "Fish and bear paws—one can't have both.\n—Chinese idiom meaning \"you can't have it both ways\""
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fae9abc7-ecd3-4042-a5b0-5f2b24491fa6.jpg?1783946101"
    }
}
