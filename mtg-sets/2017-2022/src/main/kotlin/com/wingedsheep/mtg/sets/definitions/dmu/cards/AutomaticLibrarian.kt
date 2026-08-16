package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Automatic Librarian
 * {3}
 * Artifact Creature — Construct
 * 3/2
 * When this creature enters, scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)
 */
val AutomaticLibrarian = card("Automatic Librarian") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    oracleText = "When this creature enters, scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)"
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Scry(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "229"
        artist = "Alex Konstad"
        flavorText = "Enforcing absolute quiet with extreme prejudice since 3285 AR."
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c3d7ece-0f57-4213-a0ab-a9d7c1536ebb.jpg?1783921271"
    }
}
