package com.wingedsheep.mtg.sets.definitions.hop.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Beast Hunt
 * {3}{G}
 * Sorcery
 * Reveal the top three cards of your library. Put all creature cards revealed this way into your hand and the rest into your graveyard.
 *
 * Canonical printing: Planechase (2009) is Beast Hunt's earliest real printing — it predates the
 * Zendikar printing by a month.
 *
 * The remainder goes to the graveyard rather than the pattern's default bottom-of-library. The
 * printed line names no order for it, so the cards keep their gather order ([CardOrder.Preserve])
 * rather than the pattern's bottom-of-library default of a random shuffle.
 */
val BeastHunt = card("Beast Hunt") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Reveal the top three cards of your library. Put all creature cards revealed this way " +
        "into your hand and the rest into your graveyard."

    spell {
        effect = Patterns.Library.revealTopPutAllMatchingToHand(
            count = DynamicAmount.Fixed(3),
            filter = GameObjectFilter.Creature,
            restDestination = CardDestination.ToZone(Zone.GRAVEYARD),
            restOrder = CardOrder.Preserve,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Kieran Yanner"
        flavorText = "\"Surely we could tame something besides hurdas and pillarfield oxen!\"\n—Sheyda, Ondu gamekeeper"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db385ec5-a33a-45df-a1db-e99dd3f62f6d.jpg?1783942320"
    }
}
