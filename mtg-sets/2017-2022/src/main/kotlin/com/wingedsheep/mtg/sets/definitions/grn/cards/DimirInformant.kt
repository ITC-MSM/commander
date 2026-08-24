package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dimir Informant
 * {2}{U}
 * Creature — Human Rogue
 * 1/4
 * When this creature enters, surveil 2. (Look at the top two cards of your library, then put any number of them into your graveyard and the rest on top of your library in any order.)
 */
val DimirInformant = card("Dimir Informant") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Rogue"
    oracleText = "When this creature enters, surveil 2. (Look at the top two cards of your library, then put any number of them into your graveyard and the rest on top of your library in any order.)"
    power = 1
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.surveil(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Lucas Graciano"
        flavorText = "The letters arrive, all sealed and read."
        imageUri = "https://cards.scryfall.io/normal/front/0/2/0230afeb-5ce8-436e-9afc-73cdd7baf424.jpg?1783934190"
    }
}
