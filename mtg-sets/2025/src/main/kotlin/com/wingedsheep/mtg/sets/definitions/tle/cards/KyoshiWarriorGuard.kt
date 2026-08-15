package com.wingedsheep.mtg.sets.definitions.tle.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kyoshi Warrior Guard
 * {1}{W}
 * Creature — Human Warrior Ally
 * 2/3
 *
 * Vanilla — no rules text.
 */
val KyoshiWarriorGuard = card("Kyoshi Warrior Guard") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Warrior Ally"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "216"
        artist = "Yunomachi"
        flavorText = "Kyoshi Warrior drills are designed to encourage individual discipline as well as uncompromising trust in their fellow warriors."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a2acb28-c0e2-492b-a227-ef49e905e0fb.jpg?1783904787"
    }
}
