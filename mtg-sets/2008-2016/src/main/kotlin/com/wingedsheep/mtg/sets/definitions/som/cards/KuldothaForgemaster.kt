package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Kuldotha Forgemaster — Scars of Mirrodin #169
 * {5} · Artifact Creature — Construct · 3 / 5
 *
 * {T}, Sacrifice three artifacts: Search your library for an artifact card, put it onto the
 * battlefield, then shuffle.
 *
 * The Forgemaster itself is one of the three artifacts it can eat — the sacrifice is paid as a
 * cost, so the ability is already on the stack and resolves fine without its source.
 */
val KuldothaForgemaster = card("Kuldotha Forgemaster") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 3
    toughness = 5
    oracleText = "{T}, Sacrifice three artifacts: Search your library for an artifact card, put it onto the battlefield, then shuffle."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.SacrificeMultiple(3, GameObjectFilter.Artifact),
        )
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Artifact,
            destination = SearchDestination.BATTLEFIELD,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "169"
        artist = "jD"
        flavorText = "The goblins say it used to be larger, before it began to stoke the Great Furnace with pieces of itself."
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad590bea-b872-4af7-a612-c8e8759d59df.jpg?1783941706"
    }
}
