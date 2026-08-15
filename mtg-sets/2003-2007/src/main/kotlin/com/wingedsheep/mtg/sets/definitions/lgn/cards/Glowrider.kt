package com.wingedsheep.mtg.sets.definitions.lgn.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Glowrider
 * {2}{W}
 * Creature — Human Cleric
 * 2/1
 * Noncreature spells cost {1} more to cast.
 */
val Glowrider = card("Glowrider") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 1
    oracleText = "Noncreature spells cost {1} more to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.AnyCaster(GameObjectFilter.Noncreature),
            modification = CostModification.IncreaseGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "15"
        artist = "Scott M. Fischer"
        flavorText = "\"It is not yet time.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9ad94e39-0aac-46bb-a7f2-bd88c537cb9c.jpg?1562926255"
    }
}
