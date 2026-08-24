package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Storm Front
 * {G}
 * Enchantment
 * {G}{G}: Tap target creature with flying.
 */
val StormFront = card("Storm Front") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "{G}{G}: Tap target creature with flying."

    activatedAbility {
        cost = Costs.Mana("{G}{G}")
        val flier = target("target", Targets.CreatureWithKeyword(Keyword.FLYING))
        effect = Effects.Tap(flier)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "259"
        artist = "William O'Connor"
        flavorText = "The calmest day on Rath would be thought a storm anywhere else."
        imageUri = "https://cards.scryfall.io/normal/front/9/9/994bb02d-6fef-454b-b1b1-d3d1af8dcd1a.jpg"
    }
}
