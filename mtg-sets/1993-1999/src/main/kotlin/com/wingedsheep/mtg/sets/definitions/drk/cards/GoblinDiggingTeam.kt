package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Goblin Digging Team
 * {R}
 * Creature — Goblin
 * 1/1
 * {T}, Sacrifice this creature: Destroy target Wall.
 */
val GoblinDiggingTeam = card("Goblin Digging Team") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin"
    power = 1
    toughness = 1
    oracleText = "{T}, Sacrifice this creature: Destroy target Wall."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        val wall = target(
            "target Wall",
            TargetCreature(filter = TargetFilter.Creature.withSubtype(Subtype.WALL))
        )
        effect = Effects.Destroy(wall)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Ron Spencer"
        flavorText = "\"From down here we can make the whole wall collapse!\" \"Uh, yeah, boss, but how do we get out?\""
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8a538b9d-351e-40bb-be11-9ba08c16352b.jpg?1783947935"
    }
}
