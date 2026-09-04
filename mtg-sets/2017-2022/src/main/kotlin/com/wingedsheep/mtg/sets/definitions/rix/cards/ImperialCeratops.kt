package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Imperial Ceratops
 * {4}{W}
 * Creature — Dinosaur
 * 3/5
 * Enrage — Whenever this creature is dealt damage, you gain 2 life.
 */
val ImperialCeratops = card("Imperial Ceratops") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dinosaur"
    oracleText = "Enrage — Whenever this creature is dealt damage, you gain 2 life."
    power = 3
    toughness = 5

    triggeredAbility {
        trigger = Triggers.TakesDamage
        effect = Effects.GainLife(2)
        description = "Enrage — Whenever this creature is dealt damage, you gain 2 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "10"
        artist = "Bayard Wu"
        flavorText = "\"The music of blades against its silver armor merely inspires it to bellow " +
            "more full-throated melodies.\"\n—Huatli"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5030e80-58c4-4ce3-877e-8395b653f6e8.jpg?1783935339"
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
