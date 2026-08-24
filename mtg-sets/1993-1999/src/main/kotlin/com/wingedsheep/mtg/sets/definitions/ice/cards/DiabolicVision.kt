package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

/**
 * Diabolic Vision
 * {U}{B}
 * Sorcery
 *
 * Look at the top five cards of your library. Put one of them into your hand and the rest on top of
 * your library in any order.
 *
 * Both printed sentences are one `Patterns.Library.lookAtTopAndKeep` recipe — Stock Up's shape with
 * the remainder going back on **top** instead of the bottom. The pile prompts ("Put in hand" / "Put
 * on top") are derived from the two destinations by the facade, so they are never spelled here.
 */
val DiabolicVision = card("Diabolic Vision") {
    manaCost = "{U}{B}"
    colorIdentity = "BU"
    typeLine = "Sorcery"
    oracleText = "Look at the top five cards of your library. Put one of them into your hand and the rest on top of your library in any order."

    spell {
        effect = Patterns.Library.lookAtTopAndKeep(
            count = 5,
            keepCount = 1,
            keepDestination = CardDestination.ToZone(Zone.HAND),
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Top),
            restOrder = CardOrder.ControllerChooses
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "284"
        artist = "Anthony S. Waters"
        flavorText = "\"I have seen the true path. I will not warm myself by the fire—I will become the flame.\"\n—Lim-Dûl, the Necromancer"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1ea01324-1cfb-498c-8299-f690373864bd.jpg"
    }
}
