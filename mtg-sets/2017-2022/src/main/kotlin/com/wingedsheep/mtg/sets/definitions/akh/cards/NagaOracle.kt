package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Naga Oracle
 * {3}{U}
 * Creature — Snake Cleric
 * 2/4
 * When this creature enters, surveil 3. (Look at the top three cards of your library, then put any number of them into your graveyard and the rest on top of your library in any order.)
 *
 * Printed in Amonkhet as "scry 3"; the current Oracle text is the errata'd surveil.
 */
val NagaOracle = card("Naga Oracle") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Snake Cleric"
    oracleText = "When this creature enters, surveil 3. (Look at the top three cards of your library, then put any number of them into your graveyard and the rest on top of your library in any order.)"
    power = 2
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Surveil(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "62"
        artist = "Deruchenko Alexander"
        flavorText = "\"All questions will be answered during the Hour of Revelation.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/0/9060b7b5-3ef1-443e-8f8a-450cc42c43a6.jpg?1783936518"
    }
}
