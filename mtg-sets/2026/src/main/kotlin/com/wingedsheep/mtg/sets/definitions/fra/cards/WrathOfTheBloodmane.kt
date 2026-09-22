package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

val WrathOfTheBloodmane = card("Wrath of the Bloodmane") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "This spell costs {1} less to cast if you control a legendary creature.\n" +
        "Wrath of the Bloodmane deals 4 damage to target creature or planeswalker."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfControlFilter(
                    amount = 1,
                    filter = GameObjectFilter.Creature.legendary(),
                ),
            ),
        )
    }

    spell {
        val t = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
        effect = Effects.DealDamage(4, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "96"
        artist = "Manuel Castañón"
        flavorText = "\"I can see the weakness in your soul. Let me cut it out of you!\""
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5b55617-684a-4036-be9b-a3b24fc9cd5a.jpg?1789385820"
        inBooster = false
    }
}
