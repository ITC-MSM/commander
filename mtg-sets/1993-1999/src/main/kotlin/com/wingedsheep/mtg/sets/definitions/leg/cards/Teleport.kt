package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Teleport
 * {U}{U}{U}
 * Instant
 *
 * Cast this spell only during the declare attackers step.
 * Target creature can't be blocked this turn.
 */
val Teleport = card("Teleport") {
    manaCost = "{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Cast this spell only during the declare attackers step.\n" +
        "Target creature can't be blocked this turn."

    spell {
        castOnlyDuring(Step.DECLARE_ATTACKERS)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "80"
        artist = "Douglas Shuler"
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18f86e13-f942-423e-b175-930d768cb811.jpg?1783948070"
    }
}
