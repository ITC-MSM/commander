package com.wingedsheep.mtg.sets.definitions.lgn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.dsl.DynamicAmounts

/**
 * Berserk Murlodont
 * {4}{G}
 * Creature — Beast
 * 3/3
 * Whenever a Beast becomes blocked, it gets +1/+1 until end of turn for each creature blocking it.
 */
val BerserkMurlodont = card("Berserk Murlodont") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 3
    toughness = 3
    oracleText = "Whenever a Beast becomes blocked, it gets +1/+1 until end of turn for each creature blocking it."

    triggeredAbility {
        trigger = Triggers.becomesBlocked(
            filter = GameObjectFilter.Creature.withSubtype("Beast"),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.ModifyStats(
            DynamicAmounts.numberOfBlockers(),
            DynamicAmounts.numberOfBlockers(),
            EffectTarget.TriggeringEntity
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "117"
        artist = "Arnie Swekel"
        flavorText = "\"The harder we press forward, the harder Krosa pushes us back.\" —Aven scout"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/499c4674-dd9f-4848-8447-721f842a0213.jpg?1562909903"
    }
}
