package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Snapsail Glider — Scars of Mirrodin #203
 * {3} · Artifact Creature — Construct · 2 / 2
 *
 * Metalcraft — This creature has flying as long as you control three or more artifacts.
 *
 * "Metalcraft" is an ability word (CR 207.2c) — no keyword, no rules meaning, only the oracle
 * line. The clause itself is a layer-6 [GrantKeyword] over [GroupFilter.source] wrapped in a
 * [ConditionalStaticAbility]; because the gate is re-read at projection rather than latched, the
 * Glider loses flying mid-combat if the third artifact leaves, and a creature already blocking it
 * stays a legal blocker.
 */
val SnapsailGlider = card("Snapsail Glider") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 2
    toughness = 2
    oracleText = "Metalcraft — This creature has flying as long as you control three or more artifacts."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, GroupFilter.source()),
            condition = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "203"
        artist = "Efrem Palacios"
        flavorText = "Built from a reconfigured thresher, it charges with light reflected off the golden plain, ready to take to the air in case of danger."
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc98e0af-b18e-4172-bc56-19952ebd0303.jpg?1783941697"
    }
}
