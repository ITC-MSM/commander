package com.wingedsheep.mtg.sets.definitions.gpt.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Plagued Rusalka
 * {B}
 * Creature — Spirit
 * 1/1
 *
 * {B}, Sacrifice a creature: Target creature gets -1/-1 until end of turn.
 */
val PlaguedRusalka = card("Plagued Rusalka") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    oracleText = "{B}, Sacrifice a creature: Target creature gets -1/-1 until end of turn."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}"), Costs.Sacrifice(GameObjectFilter.Creature))
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-1, -1, creature)
        description = "{B}, Sacrifice a creature: Target creature gets -1/-1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "56"
        artist = "Alex Horley-Orlandelli"
        flavorText = "\"Look at her, once filled with innocence. Death has a way of wringing away such . . . deficiencies.\"\n—Savra"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd84bbb3-8b99-4e6d-b514-b094ec93eaa0.jpg?1783943506"
    }
}
