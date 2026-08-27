package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Synchronous Sliver
 * {4}{U}
 * Creature — Sliver
 * 3/3
 * All Sliver creatures have vigilance.
 */
val SynchronousSliver = card("Synchronous Sliver") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sliver"
    power = 3
    toughness = 3
    oracleText = "All Sliver creatures have vigilance."

    staticAbility {
        ability = GrantKeyword(
            Keyword.VIGILANCE,
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "E. M. Gist"
        flavorText = "\"With a twitch of its muscles, its timeline forks. Then, just as quickly, its two selves reintegrate. Causality, strangely, seems not to mind.\"\n—Rukarumel, field journal"
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c02fa57b-4b7f-46e1-b2b5-6b1a9e9d1643.jpg"
    }
}
