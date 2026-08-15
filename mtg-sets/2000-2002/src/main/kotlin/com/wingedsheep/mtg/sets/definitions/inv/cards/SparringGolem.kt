package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sparring Golem
 * {3}
 * Artifact Creature — Golem
 * 2/2
 * Whenever this creature becomes blocked, it gets +1/+1 until end of turn for each creature blocking it.
 */
val SparringGolem = card("Sparring Golem") {
    manaCost = "{3}"
    typeLine = "Artifact Creature — Golem"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature becomes blocked, it gets +1/+1 until end of turn for each creature blocking it."

    triggeredAbility {
        trigger = Triggers.becomesBlocked(binding = TriggerBinding.SELF)
        effect = Effects.ModifyStats(
            DynamicAmounts.numberOfBlockers(),
            DynamicAmounts.numberOfBlockers(),
            EffectTarget.TriggeringEntity
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "312"
        artist = "Adam Rex"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d829d9de-83fa-4feb-8efc-0075315163c6.jpg?1562938416"
    }
}
