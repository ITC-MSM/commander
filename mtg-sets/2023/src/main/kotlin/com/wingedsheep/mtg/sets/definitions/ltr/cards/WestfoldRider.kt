package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Westfold Rider
 * {1}{W}
 * Creature — Human Knight
 * 3/1
 *
 * Sacrifice this creature: Destroy target artifact or enchantment. Activate only as a sorcery.
 */
val WestfoldRider = card("Westfold Rider") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 3
    toughness = 1
    oracleText = "Sacrifice this creature: Destroy target artifact or enchantment. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.SacrificeSelf
        val target = target("artifact or enchantment", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(target)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Anastasia Balakchina"
        flavorText = "\"The Orcs were greater in number than we counted on. Great Orcs, who also bore the White Hand of Isengard.\"\n—Éomer"
        imageUri = "https://cards.scryfall.io/normal/front/7/1/71ed2e4b-c732-472a-a589-f1f53086d9ee.jpg?1686967979"
    }
}
