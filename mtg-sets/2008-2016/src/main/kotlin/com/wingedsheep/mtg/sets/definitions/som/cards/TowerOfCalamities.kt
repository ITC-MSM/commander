package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tower of Calamities — Scars of Mirrodin #212
 * {4} · Artifact
 *
 * {8}, {T}: This artifact deals 12 damage to target creature.
 */
val TowerOfCalamities = card("Tower of Calamities") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{8}, {T}: This artifact deals 12 damage to target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{8}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(12, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "212"
        artist = "Aleksi Briclot"
        flavorText = "The ur-golems concealed one of their towers out of fear that its power would be abused, and in anticipation of a time when its power would be sorely needed."
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8a77391b-5727-4408-bb50-970f7a13a83c.jpg?1783941694"
    }
}
