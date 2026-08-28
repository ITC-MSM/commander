package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction

/**
 * Goblin Firestarter
 * {R}
 * Creature — Goblin
 * 1/1
 *
 * Sacrifice this creature: It deals 1 damage to any target. Activate only during your turn,
 * before attackers are declared.
 */
val GoblinFirestarter = card("Goblin Firestarter") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin"
    oracleText = "Sacrifice this creature: It deals 1 damage to any target. Activate only during your turn, before attackers are declared."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.SacrificeSelf
        restrictions = listOf(
            ActivationRestriction.OnlyDuringYourTurn,
            ActivationRestriction.BeforeStep(Step.DECLARE_ATTACKERS)
        )
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "96"
        artist = "Keith Parkinson"
        imageUri = "https://cards.scryfall.io/normal/front/8/4/84ee21ef-26c0-4def-9046-5d6fcfa3bfeb.jpg"
    }
}
