package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Lotus Petal
 * {0}
 * Artifact
 * {T}, Sacrifice this artifact: Add one mana of any color.
 */
val LotusPetal = card("Lotus Petal") {
    manaCost = "{0}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{T}, Sacrifice this artifact: Add one mana of any color."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "294"
        artist = "April Lee"
        flavorText = "\"Hard to imagine,\" mused Hanna, stroking the petal, \"such a lovely flower inspiring such greed.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c877da3-68fa-41d0-8a24-8c79fcd8ecc1.jpg"
    }
}
