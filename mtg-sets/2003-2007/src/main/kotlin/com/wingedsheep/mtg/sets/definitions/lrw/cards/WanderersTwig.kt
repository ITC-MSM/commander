package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Wanderer's Twig
 * {1}
 * Artifact
 * {1}, Sacrifice this artifact: Search your library for a basic land card, reveal it, put it into
 * your hand, then shuffle.
 */
val WanderersTwig = card("Wanderer's Twig") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}, Sacrifice this artifact: Search your library for a basic land card, reveal " +
        "it, put it into your hand, then shuffle."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            count = 1,
            destination = SearchDestination.HAND,
            reveal = true,
            shuffleAfter = true
        )
        description = "{1}, Sacrifice this artifact: Search your library for a basic land card, " +
            "reveal it, put it into your hand, then shuffle."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "265"
        artist = "Dave Dorman"
        flavorText = "For every tree who falls, there are countless sprouts waiting to rise."
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8ea7b2c0-c641-478f-b8d9-17aa17fa1cbe.jpg?1783942849"
    }
}
