package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Conduct Electricity
 * {4}{R}
 * Instant
 * Conduct Electricity deals 6 damage to target creature and 2 damage to
 * up to one target creature token.
 */
val ConductElectricity = card("Conduct Electricity") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Conduct Electricity deals 6 damage to target creature and 2 damage to up to one target creature token."

    spell {
        val creature = target("target creature", Targets.Creature)
        val creatureToken = target(
            "up to one target creature token",
            TargetObject(
                filter = TargetFilter(GameObjectFilter.Creature and GameObjectFilter.Token),
                optional = true
            )
        )
        effect = Effects.DealDamage(6, creature)
            .then(Effects.DealDamage(2, creatureToken))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Chris Seaman"
        flavorText = "\"Give me a lightning rod, something I can use as a channel!\" Ral called above the mayhem. Nodding, Finneas drew a copperleaf arrow from his emptying quiver and shot."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f373dd6-2412-453c-85ba-10230dfe473a.jpg?1721426597"
    }
}
