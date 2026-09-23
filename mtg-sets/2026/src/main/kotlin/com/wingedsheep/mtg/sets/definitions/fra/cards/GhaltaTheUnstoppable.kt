package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty

val GhaltaTheUnstoppable = card("Ghalta the Unstoppable") {
    manaCost = "{8}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Elder Dinosaur"
    oracleText = "This spell costs {X} less to cast, where X is the greatest power among creatures you control.\n" +
        "Trample\n" +
        "Other creatures you control have trample."
    power = 8
    toughness = 8

    // Reduces only the generic part, so Ghalta never costs less than {G}.
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.GreatestPropertyAmongPermanentsYouControl(
                    EntityNumericProperty.Power, Filters.Creature
                )
            ),
        )
    }

    keywords(Keyword.TRAMPLE)

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.TRAMPLE,
            filter = GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "260"
        artist = "Antonio José Manzanedo"
        flavorText = "The apex of nature's ferocity."
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1d535b5f-c916-4f16-89a7-9477578826d2.jpg?1788878290"
        inBooster = false
    }
}
