package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Xenic Poltergeist
 * {1}{B}{B}
 * Creature — Spirit
 * 1/1
 * {T}: Until your next upkeep, target noncreature artifact becomes an artifact creature with power
 *   and toughness each equal to its mana value.
 *
 * Antiquities engine gap: animating a single target with *dynamic* P/T equal to the target's own
 * mana value. [Effects.BecomeCreatureWithManaValueStats] (a new facade over the extended
 * [com.wingedsheep.sdk.scripting.effects.BecomeCreatureEffect], which now accepts optional dynamic
 * P/T) sets base P/T via a Layer 7b `SetPowerToughnessDynamic(ManaValue, ManaValue)` keyed to the
 * affected entity, adds the CREATURE type (the artifact stays an artifact via `addTypes`), and
 * lasts until the controller's next upkeep ([Duration.UntilYourNextUpkeep]). The target must be a
 * noncreature artifact (`GameObjectFilter.Artifact.notCreature()`).
 */
val XenicPoltergeist = card("Xenic Poltergeist") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 1

    oracleText = "{T}: Until your next upkeep, target noncreature artifact becomes an artifact " +
        "creature with power and toughness each equal to its mana value."

    activatedAbility {
        cost = Costs.Tap
        val t = target(
            "target noncreature artifact",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Artifact.notCreature()))
        )
        effect = Effects.BecomeCreatureWithManaValueStats(
            target = t,
            addTypes = setOf("ARTIFACT"),
            duration = Duration.UntilYourNextUpkeep
        )
        description = "{T}: Until your next upkeep, target noncreature artifact becomes an artifact " +
            "creature with power and toughness each equal to its mana value."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "20"
        artist = "Dan Frazier"
        imageUri = "https://cards.scryfall.io/normal/front/5/1/5149ffff-d38f-458e-bcfa-a4b6b332a0b4.jpg?1562911994"
    }
}
