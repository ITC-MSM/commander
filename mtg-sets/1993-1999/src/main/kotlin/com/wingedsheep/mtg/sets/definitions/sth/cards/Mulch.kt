package com.wingedsheep.mtg.sets.definitions.sth.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Mulch — Stronghold #110
 * {1}{G} · Sorcery
 *
 * Reveal the top four cards of your library. Put all land cards revealed this way into your hand
 * and the rest into your graveyard.
 *
 * The Marina Vendrell recipe with the remainder routed one zone over:
 * [Patterns.Library.revealTopPutAllMatchingToHand] gathers the top four, reveals them publicly, and
 * partitions them with a choice-free `FilterCollection` — every land card goes to hand, everything
 * else to `restDestination`. Mulch's remainder is the graveyard rather than the bottom of the
 * library, and it names no order, so the cards keep their gathered (top-down) order
 * ([CardOrder.Preserve]) instead of the recipe's default reshuffle: a random order is a real
 * instruction ("in a random order") that this card doesn't print.
 *
 * The reveal is mandatory and the partition is automatic — there is no "up to one" choice here, so
 * nothing pauses for player input and a library holding fewer than four cards simply reveals what
 * is left.
 */
val Mulch = card("Mulch") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Reveal the top four cards of your library. Put all land cards revealed this way " +
        "into your hand and the rest into your graveyard."

    spell {
        effect = Patterns.Library.revealTopPutAllMatchingToHand(
            count = DynamicAmount.Fixed(4),
            filter = GameObjectFilter.Land,
            restDestination = CardDestination.ToZone(Zone.GRAVEYARD),
            restOrder = CardOrder.Preserve,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "110"
        artist = "Rebecca Guay"
        flavorText = "Hope is the one crop that can grow in any climate."
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4cf9e9a9-325a-4010-acb8-1406adcaeca9.jpg?1783946551"
    }
}
