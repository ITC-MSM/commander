package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Corrupted Harvester
 * {4}{B}{B}
 * Creature — Phyrexian Horror
 * 6/3
 *
 * {B}, Sacrifice a creature: Regenerate this creature.
 *
 * The sacrifice is unrestricted beyond "a creature", so the Harvester can eat itself — a legal but
 * pointless activation, since the shield it buys has nothing left to protect.
 */
val CorruptedHarvester = card("Corrupted Harvester") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Horror"
    power = 6
    toughness = 3
    oracleText = "{B}, Sacrifice a creature: Regenerate this creature."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{B}"),
            Costs.Sacrifice(GameObjectFilter.Creature)
        )
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{B}, Sacrifice a creature: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "59"
        artist = "Nils Hamm"
        flavorText = "\"Before the blessed assault begins, we must seek specimens that are well-adapted to our way of . . . life.\"\n—Sheoldred, Whispering One"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b54625ac-484f-4522-8048-38e01c545ac3.jpg?1783941733"
    }
}
