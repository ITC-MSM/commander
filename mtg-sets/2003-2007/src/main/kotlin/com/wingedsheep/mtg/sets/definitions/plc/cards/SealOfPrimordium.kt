package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Seal of Primordium
 * {1}{G}
 * Enchantment
 * Sacrifice this enchantment: Destroy target artifact or enchantment.
 *
 * The Planar Chaos green Seal of Cleansing. The target is chosen on activation, so the
 * sacrifice cost is paid before the destroy resolves — the Seal is already gone when it does.
 */
val SealOfPrimordium = card("Seal of Primordium") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Sacrifice this enchantment: Destroy target artifact or enchantment."

    activatedAbility {
        cost = Costs.SacrificeSelf
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
        description = "Sacrifice this enchantment: Destroy target artifact or enchantment."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "153"
        artist = "Christopher Moeller"
        flavorText = "\"I am the simplifier, the root that drags all artifice to earth.\"\n—Seal inscription"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2ba91ee-1f8e-47db-bb2d-bbb62039db17.jpg"
    }
}
