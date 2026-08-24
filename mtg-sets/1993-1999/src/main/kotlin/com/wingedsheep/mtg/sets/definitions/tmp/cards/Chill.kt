package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Chill
 * {1}{U}
 * Enchantment
 * Red spells cost {2} more to cast.
 */
val Chill = card("Chill") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Red spells cost {2} more to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.AnyCaster(GameObjectFilter.Any.withColor(Color.RED)),
            modification = CostModification.IncreaseGeneric(2)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "56"
        artist = "Greg Simanson"
        flavorText = "\"Temper, temper.\"\n" +
            "—Ertai, wizard adept"
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5a7bd777-6f11-441e-887f-9cee1ef96035.jpg"
    }
}
