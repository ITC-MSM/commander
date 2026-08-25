package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Courier's Capsule
 * {1}{U}
 * Artifact
 * {1}{U}, {T}, Sacrifice this artifact: Draw two cards.
 *
 * The blue member of the Alara capsule cycle, and structurally identical to [DispellersCapsule]: a
 * three-part [Costs.Composite] of mana, [Costs.Tap] and [Costs.SacrificeSelf] paying for an
 * untargeted [Effects.DrawCards].
 */
val CouriersCapsule = card("Courier's Capsule") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact"
    oracleText = "{1}{U}, {T}, Sacrifice this artifact: Draw two cards."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Andrew Murray"
        flavorText = "In ages past, Esper couriers bore messages written on ornate scrolls. The medium has grown more sophisticated, but the principle remains the same."
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39ae12ff-8039-4aac-aa50-f879376888a1.jpg"
    }
}
