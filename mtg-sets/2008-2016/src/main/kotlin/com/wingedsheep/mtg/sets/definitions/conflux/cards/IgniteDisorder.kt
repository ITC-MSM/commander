package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DividedDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Ignite Disorder
 * {1}{R}
 * Instant
 * Ignite Disorder deals 3 damage divided as you choose among one, two, or three target white
 * and/or blue creatures.
 *
 * The Arc Lightning shape: **one** target requirement with `count = 3, minCount = 1` plus a
 * [DividedDamageEffect], not three separate targets — the engine reads the requirement's range to
 * ask for a damage distribution at cast time and validates that every chosen target gets at least
 * one damage. "White and/or blue" is a single noun phrase, so it is one filter:
 * [GameObjectFilter.withAnyColor] unions the two colours into a single `Or` predicate rather than
 * two independent clauses.
 */
val IgniteDisorder = card("Ignite Disorder") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Ignite Disorder deals 3 damage divided as you choose among one, two, or three " +
        "target white and/or blue creatures."

    spell {
        target(
            "target",
            TargetCreature(
                count = 3,
                minCount = 1,
                filter = TargetFilter(
                    GameObjectFilter.Creature.withAnyColor(Color.WHITE, Color.BLUE)
                )
            )
        )
        effect = DividedDamageEffect(
            totalDamage = 3,
            minTargets = 1,
            maxTargets = 3
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "66"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "\"Bant is a world imprisoned by polished stone and tyrannical rule. It yearns to strike back against those who restrain it.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e38419d9-e321-4642-aad4-99c5489f8fa8.jpg"
    }
}
