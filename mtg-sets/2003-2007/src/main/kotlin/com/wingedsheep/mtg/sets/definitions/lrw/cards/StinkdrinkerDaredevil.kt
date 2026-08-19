package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Stinkdrinker Daredevil
 * {2}{R}
 * Creature — Goblin Rogue
 * 1/3
 * Giant spells you cast cost {2} less to cast.
 */
val StinkdrinkerDaredevil = card("Stinkdrinker Daredevil") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    power = 1
    toughness = 3
    oracleText = "Giant spells you cast cost {2} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype(Subtype.GIANT)),
            modification = CostModification.ReduceGeneric(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "191"
        artist = "Pete Venters"
        flavorText = "Boggarts constantly strive to outdo each other with the things they bring back to the warren, each hoping the exploit will become as well-known as those of Auntie Grub."
        imageUri = "https://cards.scryfall.io/normal/front/4/2/428ba328-a0d8-4c52-ac2c-e9698486cc08.jpg?1783942869"
    }
}
