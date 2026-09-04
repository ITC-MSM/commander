package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Frilled Deathspitter
 * {2}{R}
 * Creature — Dinosaur
 * 3/2
 * Enrage — Whenever this creature is dealt damage, it deals 2 damage to target opponent or
 * planeswalker.
 */
val FrilledDeathspitter = card("Frilled Deathspitter") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    oracleText = "Enrage — Whenever this creature is dealt damage, it deals 2 damage to target " +
        "opponent or planeswalker."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.TakesDamage
        val victim = target("target opponent or planeswalker", Targets.OpponentOrPlaneswalker)
        effect = Effects.DealDamage(2, victim)
        description = "Enrage — Whenever this creature is dealt damage, it deals 2 damage to " +
            "target opponent or planeswalker."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "104"
        artist = "Zoltan Boros"
        flavorText = "Nothing says trouble like a reedy hiss with a hint of liquid gurgle."
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3825798-f673-4d8a-9997-ccb73681cbf2.jpg?1783935298"
        ruling(
            "2018-01-19",
            "If your life total is brought to 0 or less at the same time that Frilled " +
                "Deathspitter is dealt damage, you lose the game before its enrage ability resolves."
        )
        ruling(
            "2018-01-19",
            "If multiple sources deal damage to a creature with an enrage ability at the same " +
                "time, most likely because multiple creatures blocked that creature, the enrage " +
                "ability triggers only once."
        )
        ruling(
            "2018-01-19",
            "If lethal damage is dealt to a creature with an enrage ability, that ability " +
                "triggers. The creature with that enrage ability leaves the battlefield before " +
                "that ability resolves, so it won't be affected by the resolving ability."
        )
    }
}
