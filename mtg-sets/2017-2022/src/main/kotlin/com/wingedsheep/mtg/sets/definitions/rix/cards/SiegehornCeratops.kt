package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Siegehorn Ceratops
 * {G}{W}
 * Creature — Dinosaur
 * 2/2
 * Enrage — Whenever this creature is dealt damage, put two +1/+1 counters on it.
 */
val SiegehornCeratops = card("Siegehorn Ceratops") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Dinosaur"
    oracleText = "Enrage — Whenever this creature is dealt damage, put two +1/+1 counters on it. " +
        "(It must survive the damage to get the counters.)"
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.TakesDamage
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
        description = "Enrage — Whenever this creature is dealt damage, put two +1/+1 counters on it."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "171"
        artist = "Filip Burburan"
        flavorText = "To a siegehorn, there is no such thing as a dead end."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a9c4c63-402e-489e-ab0d-1c98309b010a.jpg?1783935269"
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
