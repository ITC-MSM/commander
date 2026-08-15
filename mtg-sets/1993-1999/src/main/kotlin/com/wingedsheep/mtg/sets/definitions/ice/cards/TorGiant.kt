package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tor Giant
 * {3}{R}
 * Creature — Giant
 * 3/3
 *
 * Vanilla — no rules text.
 */
val TorGiant = card("Tor Giant") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "220"
        artist = "Douglas Shuler"
        flavorText = "\"What do you do then? Run. Run very fast. Don't stop until you see the camp—or a bigger Giant.\"\n—Toothlicker Harj, Orcish Captain"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7ef8f279-1a10-4685-99d6-bc971a7f922b.jpg?1783947482"
    }
}
