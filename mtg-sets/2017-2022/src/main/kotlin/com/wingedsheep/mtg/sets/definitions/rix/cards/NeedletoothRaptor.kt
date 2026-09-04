package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Needletooth Raptor
 * {3}{R}
 * Creature — Dinosaur
 * 2/2
 * Enrage — Whenever this creature is dealt damage, it deals 5 damage to target creature an
 * opponent controls.
 */
val NeedletoothRaptor = card("Needletooth Raptor") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    oracleText = "Enrage — Whenever this creature is dealt damage, it deals 5 damage to target " +
        "creature an opponent controls."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.TakesDamage
        val victim = target(
            "target creature an opponent controls",
            Targets.CreatureOpponentControls
        )
        effect = Effects.DealDamage(5, victim)
        description = "Enrage — Whenever this creature is dealt damage, it deals 5 damage to " +
            "target creature an opponent controls."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "107"
        artist = "Winona Nelson"
        flavorText = "It hatches with its ferocity fully grown."
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e9a90b68-d5f4-4f3c-bd4b-af59dd868919.jpg?1783935298"
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
