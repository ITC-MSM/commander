package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Swallowed by Leviathan
 * {2}{U}
 * Instant
 *
 * Choose target spell. Surveil 2, then counter the chosen spell unless its controller pays
 * {1} for each card in your graveyard.
 *
 * Pure composition over existing effects, ordered to match the printed sequence:
 *  1. [Effects.Surveil]`(2)` — resolves *first*, so any cards milled into your graveyard
 *     count toward the tax below (the card is intentionally a "fill your own graveyard to
 *     raise the price" spell).
 *  2. [Effects.CounterUnlessDynamicPays] with [DynamicAmount.Count] over your graveyard —
 *     "{1} for each card in your graveyard", evaluated when this step runs (post-surveil).
 *     Swallowed by Leviathan and the chosen spell are still on the stack, so neither is
 *     counted.
 */
val SwallowedByLeviathan = card("Swallowed by Leviathan") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose target spell. Surveil 2, then counter the chosen spell unless its " +
        "controller pays {1} for each card in your graveyard. (To surveil 2, look at the top " +
        "two cards of your library, then put any number of them into your graveyard and the " +
        "rest on top of your library in any order.)"

    spell {
        target = Targets.Spell
        effect = Effects.Composite(
            Effects.Surveil(2),
            Effects.CounterUnlessDynamicPays(
                amount = DynamicAmount.Count(Player.You, Zone.GRAVEYARD, GameObjectFilter.Any),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "79"
        artist = "Sansyu"
        imageUri = "https://cards.scryfall.io/normal/front/2/2/2270642d-fe2a-4265-aff0-a24a43ebe0a1.jpg?1756047948"
    }
}
