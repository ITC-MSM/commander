package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Legion's Judgment
 * {2}{W}
 * Sorcery
 *
 * Destroy target creature with power 4 or greater.
 */
val LegionsJudgment = card("Legion's Judgment") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature with power 4 or greater."

    spell {
        val victim = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.powerAtLeast(4)))
        )
        effect = Effects.Destroy(victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "21"
        artist = "Lucas Graciano"
        flavorText = "\"My lance was once wielded by Venerable Tarrian. In his name and by his might, I cast you down!\""
        imageUri = "https://cards.scryfall.io/normal/front/3/8/385bea20-c196-4da8-bc3e-36f8d50dcc17.jpg"
    }
}
