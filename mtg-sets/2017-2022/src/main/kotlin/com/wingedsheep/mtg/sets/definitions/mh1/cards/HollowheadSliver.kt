package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Hollowhead Sliver
 * {2}{R}
 * Creature — Sliver
 * 2/2
 * Sliver creatures you control have "{T}, Discard a card: Draw a card."
 *
 * The quoted-ability Sliver lord: [GrantActivatedAbility] hands the same looting ability to every
 * Sliver creature you control, this one included. The cost is the composite of [Costs.Tap] and
 * [Costs.DiscardCard] — a plain "discard a card", no filter and no randomness.
 */
val HollowheadSliver = card("Hollowhead Sliver") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "Sliver creatures you control have \"{T}, Discard a card: Draw a card.\""

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                cost = Costs.Composite(Costs.Tap, Costs.DiscardCard),
                effect = Effects.DrawCards(1)
            ),
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "132"
        artist = "Johan Grenier"
        flavorText = "\"Brilliant! They evolved away from energy-taxing brains and respond only to spinal reflex arcs from the hive mind.\"\n—Rukarumel, field journal"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f05b162-90aa-4c95-903f-775a17d20359.jpg?1783933111"
    }
}
