package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lightning-Core Excavator
 * {1}
 * Artifact Creature — Golem
 * 0/3
 *
 * {5}, {T}, Sacrifice this creature: It deals 3 damage to any target.
 */
val LightningCoreExcavator = card("Lightning-Core Excavator") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    oracleText = "{5}, {T}, Sacrifice this creature: It deals 3 damage to any target."
    power = 0
    toughness = 3

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap, Costs.SacrificeSelf)
        target = Targets.Any
        effect = Effects.DealDamage(3, EffectTarget.ContextTarget(0))
        description = "{5}, {T}, Sacrifice this creature: It deals 3 damage to any target."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Cristi Balanescu"
        flavorText = "\"That's the third time this month it's exploded on site. Maybe we should switch to something a little less defective.\"\n—Kerrin, archaeologist"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1b959af-bb23-42e7-8848-7405ed597c8d.jpg?1783930499"
    }
}
