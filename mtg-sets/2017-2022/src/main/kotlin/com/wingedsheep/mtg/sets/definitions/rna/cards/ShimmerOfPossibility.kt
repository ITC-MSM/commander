package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

/**
 * Shimmer of Possibility — Ravnica Allegiance #51
 * {1}{U} · Sorcery
 *
 * [Patterns.Library] `lookAtTopAndKeep` is the gather → select → move-kept → move-rest pipeline;
 * the printed "in a random order" is its `restOrder`, which matters because the three losers go
 * under the library in an order the caster does not choose.
 */
val ShimmerOfPossibility = card("Shimmer of Possibility") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Look at the top four cards of your library. Put one of them into your hand and the rest on the bottom of your library in a random order."

    spell {
        effect = Patterns.Library.lookAtTopAndKeep(
            count = 4,
            keepCount = 1,
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
            restOrder = CardOrder.Random
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "51"
        artist = "Volkan Baǵa"
        flavorText = "\"There's something peculiar about the rain today.\"\n" +
        "—Janoc, Tin Street tinker"
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76e3092d-2422-438c-b5dd-bf8eca33a76e.jpg"
    }
}
