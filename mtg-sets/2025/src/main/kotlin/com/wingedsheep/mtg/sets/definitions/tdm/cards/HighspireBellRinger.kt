package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Highspire Bell-Ringer
 * {2}{U}
 * Creature — Djinn Monk
 * 1/4
 *
 * Flying
 * The second spell you cast each turn costs {1} less to cast.
 */
val HighspireBellRinger = card("Highspire Bell-Ringer") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Djinn Monk"
    power = 1
    toughness = 4
    oracleText = "Flying\nThe second spell you cast each turn costs {1} less to cast."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any),
            modification = CostModification.ReduceGeneric(1),
            gating = CostGating.NthOfTypePerTurn(2),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "47"
        artist = "Zoltan Boros"
        flavorText = "The sequence of chimes rang out a specific alarm: dragonstorm, half a day, southwest."
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e75dccf7-2894-4c4a-b516-3eee73acddd3.jpg?1743204148"
    }
}
