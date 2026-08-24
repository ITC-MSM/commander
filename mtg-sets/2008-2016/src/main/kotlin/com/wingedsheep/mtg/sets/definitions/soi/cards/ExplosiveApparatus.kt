package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Explosive Apparatus
 * {1}
 * Artifact
 * {3}, {T}, Sacrifice this artifact: It deals 2 damage to any target.
 */
val ExplosiveApparatus = card("Explosive Apparatus") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{3}, {T}, Sacrifice this artifact: It deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "255"
        artist = "Lindsey Look"
        flavorText = "\"Souls are volatile things. When compressed and loaded into a handheld device, their destructive potential is quite impressive.\"\n" +
            "—Dierk, geistmage"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7329a19-2f68-4d2c-a725-e0d862cd234e.jpg"
    }
}
