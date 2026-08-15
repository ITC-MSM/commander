package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tribal Flames
 * {1}{R}
 * Sorcery
 * Domain — Tribal Flames deals X damage to any target, where X is the number of
 * basic land types among lands you control.
 */
val TribalFlames = card("Tribal Flames") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Domain — Tribal Flames deals X damage to any target, where X is the number of basic land types among lands you control."

    spell {
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(DynamicAmounts.domain(), t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "176"
        artist = "Tony Szczudlo"
        flavorText = "\"Fire is the universal language.\"\n—Jhoira, master artificer"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b32531e-c759-4603-abd0-1724e8df70db.jpg?1562926326"
    }
}
