package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Warren Soultrader
 * {2}{B}
 * Creature — Zombie Goblin Wizard
 * 3/3
 *
 * Pay 1 life, Sacrifice another creature: Create a Treasure token.
 *
 * Modeling notes:
 *  - The cost is a two-atom [Costs.Composite]: a life payment plus a sacrifice. "**Another**
 *    creature" is [Costs.SacrificeAnother], which sets `excludeSelf` so the Soultrader can never
 *    eat itself — the printed word is a cost restriction, not a targeting one.
 *  - There is deliberately no `{T}` in the cost, so the ability can be activated as many times as
 *    there are creatures to feed it (and life to pay) — including at instant speed in response to
 *    removal.
 */
val WarrenSoultrader = card("Warren Soultrader") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Goblin Wizard"
    power = 3
    toughness = 3
    oracleText = "Pay 1 life, Sacrifice another creature: Create a Treasure token. (It's an " +
        "artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"

    activatedAbility {
        cost = Costs.Composite(
            Costs.PayLife(1),
            Costs.SacrificeAnother(GameObjectFilter.Creature)
        )
        effect = Effects.CreateTreasure()
        description = "Pay 1 life, Sacrifice another creature: Create a Treasure token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "110"
        artist = "Pete Venters"
        flavorText = "The living take their souls for granted. The dead know what they're worth."
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b334e4c6-d316-4141-8889-f95afcc04701.jpg?1784634565"
    }
}
