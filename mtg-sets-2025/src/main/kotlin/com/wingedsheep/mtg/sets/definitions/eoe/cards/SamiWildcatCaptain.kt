package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Sami, Wildcat Captain
 * {4}{R}{W}
 * Legendary Creature — Human Artificer Rogue
 * 4/4
 *
 * Double strike, vigilance
 * Spells you cast have affinity for artifacts. (They cost {1} less to cast for each
 * artifact you control.)
 *
 * Per CR 702.41a, "affinity for artifacts" means "this spell costs {1} less to cast for
 * each artifact you control" — granting affinity to your spells is mechanically identical
 * to a battlefield-sourced [SpellCostTarget.YouCast] reduction by the artifact count, so
 * no dedicated "granted affinity" engine path is needed.
 */
val SamiWildcatCaptain = card("Sami, Wildcat Captain") {
    manaCost = "{4}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Human Artificer Rogue"
    power = 4
    toughness = 4
    oracleText = "Double strike, vigilance\n" +
        "Spells you cast have affinity for artifacts. (They cost {1} less to cast for each artifact you control.)"

    keywords(Keyword.DOUBLE_STRIKE, Keyword.VIGILANCE)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any),
            modification = CostModification.ReduceGenericBy(CostReductionSource.ArtifactsYouControl),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "226"
        artist = "Kieran Yanner"
        flavorText = "\"The hard way it is.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/e/bed64207-9193-4770-8f8f-e3203289d5a6.jpg?1752947484"
    }
}
