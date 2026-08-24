package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Omenspeaker
 * {1}{U}
 * Creature — Human Wizard
 * 1/3
 * When this creature enters, scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)
 */
val Omenspeaker = card("Omenspeaker") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 3
    oracleText = "When this creature enters, scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.scry(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Dallas Williams"
        flavorText = "Her prophecies amaze her even as she speaks them."
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f347eb88-7d1d-4ed5-b841-2bf81f00d5f0.jpg"
    }
}
