package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty

/**
 * Molten Monstrosity
 * {7}{R}
 * Creature — Hellion
 * 5/5
 * This spell costs {X} less to cast, where X is the greatest power among creatures you control.
 * Trample
 */
val MoltenMonstrosity = card("Molten Monstrosity") {
    manaCost = "{7}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Hellion"
    oracleText = "This spell costs {X} less to cast, where X is the greatest power among creatures you control.\nTrample"
    power = 5
    toughness = 5

    keywords(Keyword.TRAMPLE)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.GreatestPropertyAmongPermanentsYouControl(
                    EntityNumericProperty.Power,
                    GameObjectFilter.Creature
                )
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Manuel Castañón"
        flavorText = "The cold snap killed off most of the heat-loving hellions . . . leaving only the strongest alive to breed."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/240957e5-ab0a-443f-92de-ae999b08c44f.jpg?1783921312"
    }
}
