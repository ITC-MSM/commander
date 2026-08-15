package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Jhoira's Familiar
 * {4}
 * Artifact Creature — Bird
 * 2/2
 * Flying
 * Historic spells you cast cost {1} less to cast. (Artifacts, legendaries, and Sagas are historic.)
 */
val JhoirasFamiliar = card("Jhoira's Familiar") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Bird"
    power = 2
    toughness = 2
    oracleText = "Flying\nHistoric spells you cast cost {1} less to cast. (Artifacts, legendaries, and Sagas are historic.)"

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Historic),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "220"
        artist = "Kev Walker"
        flavorText = "\"You could say it was my pet project.\" —Jhoira"
        imageUri = "https://cards.scryfall.io/normal/front/3/7/3718761f-feb1-46c4-aaa3-7e07a3fa72fa.jpg?1562734027"
        ruling("2020-08-07", "A spell is historic if it has the legendary supertype, the artifact card type, or the Saga enchantment subtype.")
        ruling("2020-08-07", "The last ability of Jhoira's Familiar doesn't reduce its own cost while you're casting it.")
    }
}
