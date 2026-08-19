package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Moonglove Extract
 * {3}
 * Artifact
 * Sacrifice this artifact: It deals 2 damage to any target.
 */
val MoongloveExtract = card("Moonglove Extract") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Sacrifice this artifact: It deals 2 damage to any target."

    activatedAbility {
        cost = Costs.SacrificeSelf
        val recipient = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, recipient)
        description = "Sacrifice this artifact: It deals 2 damage to any target."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "258"
        artist = "Terese Nielsen"
        flavorText = "Diluted, moonglove can etch living tissue. Concentrated, a drop will kill a giant."
        imageUri = "https://cards.scryfall.io/normal/front/9/7/97377416-35e8-4cf3-be1f-edc2ec3f6eb2.jpg?1783942851"
    }
}
