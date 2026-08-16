package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vial of Dragonfire
 * {2}
 * Artifact
 * {2}, {T}, Sacrifice this artifact: It deals 2 damage to target creature.
 *
 * A single activated ability whose cost composes [Costs.Mana], [Costs.Tap] and [Costs.SacrificeSelf];
 * the effect is a plain [Effects.DealDamage] attributed to the artifact itself.
 */
val VialOfDragonfire = card("Vial of Dragonfire") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{2}, {T}, Sacrifice this artifact: It deals 2 damage to target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(2, creature)
        description = "It deals 2 damage to target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "247"
        artist = "Franz Vohwinkel"
        flavorText = "Designed by an ancient artificer, the vials are strong enough to hold the very breath of a dragon—until it's needed."
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e50e88fe-307a-4944-9233-14df4e0bb775.jpg?1783938567"
    }
}
