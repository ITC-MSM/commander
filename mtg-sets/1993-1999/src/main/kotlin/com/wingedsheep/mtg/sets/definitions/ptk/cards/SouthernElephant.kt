package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Southern Elephant
 * {3}{G}
 * Creature — Elephant
 * 3/4
 *
 * Vanilla — no rules text.
 */
val SouthernElephant = card("Southern Elephant") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elephant"
    power = 3
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Wang Yuqun"
        flavorText = "While defending their southern borders, both the Wu and Shu kingdoms fought against the barbarians' trained elephants."
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87e138c7-1166-4e20-b039-e94c1319ad42.jpg?1783946098"
    }
}
