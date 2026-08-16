package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Fauna Shaman
 * {1}{G}
 * Creature — Elf Shaman
 * 2/2
 * {G}, {T}, Discard a creature card: Search your library for a creature card, reveal it, put it into
 * your hand, then shuffle.
 */
val FaunaShaman = card("Fauna Shaman") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Shaman"
    power = 2
    toughness = 2
    oracleText = "{G}, {T}, Discard a creature card: Search your library for a creature card, " +
        "reveal it, put it into your hand, then shuffle."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{G}"),
            Costs.Tap,
            Costs.Discard(GameObjectFilter.Creature),
        )
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Creature,
            destination = SearchDestination.HAND,
            reveal = true,
        )
        description = "Search your library for a creature card, reveal it, put it into your hand, then shuffle."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "172"
        artist = "Steve Prescott"
        flavorText = "She wears the talismans of every creature she can evoke."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c685e4c3-eb7b-4b9e-9676-395d69d80974.jpg?1783941798"
    }
}
