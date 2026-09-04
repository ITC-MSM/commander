package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cacophodon
 * {3}{G}
 * Creature — Dinosaur
 * 2/5
 * Enrage — Whenever this creature is dealt damage, untap target permanent.
 */
val Cacophodon = card("Cacophodon") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    oracleText = "Enrage — Whenever this creature is dealt damage, untap target permanent."
    power = 2
    toughness = 5

    triggeredAbility {
        trigger = Triggers.TakesDamage
        val permanent = target("target permanent", Targets.Permanent)
        effect = Effects.Untap(permanent)
        description = "Enrage — Whenever this creature is dealt damage, untap target permanent."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "123"
        artist = "Johann Bodin"
        flavorText = "When a cacophodon is angry, the whole jungle knows."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/351b213e-b23e-4287-947a-6bd81f1cf751.jpg?1783935290"
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
