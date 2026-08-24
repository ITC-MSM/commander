package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Auriok Edgewright — Scars of Mirrodin #3
 * {W}{W} · Creature — Human Soldier · 2 / 2
 *
 * Metalcraft — This creature has double strike as long as you control three or more artifacts.
 *
 * "Metalcraft" is an ability word (CR 207.2c): pure flavour with no rules meaning of its own, so
 * there is no `Keyword.METALCRAFT` and nothing but the oracle line records it. What it introduces
 * is an ordinary conditional static ability — a layer-6 [GrantKeyword] over `GroupFilter.source()`
 * gated by [Conditions.YouControlAtLeast], recomputed at projection so double strike appears and
 * disappears with the third artifact rather than being latched at any point in time.
 */
val AuriokEdgewright = card("Auriok Edgewright") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 2
    oracleText = "Metalcraft — This creature has double strike as long as you control three or more artifacts."

    staticAbility {
        condition = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact)
        ability = GrantKeyword(Keyword.DOUBLE_STRIKE, GroupFilter.source())
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "3"
        artist = "Mike Bierek"
        flavorText = "Auriok soldiers craft their own weapons, forging a connection to the steel with each blow of the hammer."
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f76b18a-396b-41f5-b34b-ac232b7f316b.jpg?1783941747"
    }
}
