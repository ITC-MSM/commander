package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect

/**
 * Herbal Poultice
 * {0}
 * Artifact
 * {3}, Sacrifice this artifact: Regenerate target creature.
 */
val HerbalPoultice = card("Herbal Poultice") {
    manaCost = "{0}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{3}, Sacrifice this artifact: Regenerate target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.SacrificeSelf)
        val creature = target("target creature", Targets.Creature)
        effect = RegenerateEffect(creature)
        description = "{3}, Sacrifice this artifact: Regenerate target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "257"
        artist = "Scott Hampton"
        flavorText = "\"Apply orange leaf to a wound at dawn to clean it, at dusk to prevent the same injury from happening again.\"\n—Kithkin superstition"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b20925a3-dd4f-477c-806a-a3ec0fd2e00d.jpg?1783942851"
    }
}
