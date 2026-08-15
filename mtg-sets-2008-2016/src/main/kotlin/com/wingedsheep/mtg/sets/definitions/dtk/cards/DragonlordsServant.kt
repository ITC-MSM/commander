package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Dragonlord's Servant
 * {1}{R}
 * Creature — Goblin Shaman
 * 1/3
 * Dragon spells you cast cost {1} less to cast.
 */
val DragonlordsServant = card("Dragonlord's Servant") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Shaman"
    power = 1
    toughness = 3
    oracleText = "Dragon spells you cast cost {1} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype("Dragon")),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "138"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0ffcdd54-b6be-4d42-82c0-ae927037e859.jpg?1562782618"
    }
}
