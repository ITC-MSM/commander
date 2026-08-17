package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

/**
 * A Little Chat
 * {1}{U}
 * Instant
 * Casualty 1 (As you cast this spell, you may sacrifice a creature with power 1 or greater. When you do, copy this spell.)
 * Look at the top two cards of your library. Put one of them into your hand and the other on the bottom of your library.
 *
 * Casualty 1 (CR 702.153) is the printed [KeywordAbility.Casualty] — the cast flow surfaces the
 * optional sacrifice and queues the reflexive copy.
 *
 * The dig is exactly [Patterns.Library.lookAtTopAndKeep] with count = 2 / keepCount = 1: the kept
 * card goes to hand (the facade default) and the remainder to the bottom of the library, which is
 * also where the "Put in hand" / "Put on bottom" selection labels come from.
 */
val ALittleChat = card("A Little Chat") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Casualty 1 (As you cast this spell, you may sacrifice a creature with power 1 or greater. When you do, copy this spell.)\nLook at the top two cards of your library. Put one of them into your hand and the other on the bottom of your library."

    keywordAbility(KeywordAbility.casualty(1))

    spell {
        effect = Patterns.Library.lookAtTopAndKeep(
            count = 2,
            keepCount = 1,
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "47"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d7424b6-b56a-47b7-8204-294d3dca925f.jpg?1783923145"
    }
}
