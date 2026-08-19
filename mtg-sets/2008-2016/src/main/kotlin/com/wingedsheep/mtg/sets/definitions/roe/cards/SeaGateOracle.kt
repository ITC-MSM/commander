package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

/**
 * Sea Gate Oracle
 * {2}{U}
 * Creature — Human Wizard
 * 1/3
 *
 * When this creature enters, look at the top two cards of your library. Put one of them into your hand and the other on the bottom of your library.
 */
val SeaGateOracle = card("Sea Gate Oracle") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText = "When this creature enters, look at the top two cards of your library. Put one of them into your hand and the other on the bottom of your library."
    power = 1
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopAndKeep(
            count = 2,
            keepCount = 1,
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
        )
        description = "When this creature enters, look at the top two cards of your library. Put one " +
            "of them into your hand and the other on the bottom of your library."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "85"
        artist = "Daniel Ljunggren"
        flavorText = "\"The secret entrance should be near.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd056c91-5026-41ea-bc9e-68078d78ca82.jpg?1783941992"
    }
}
