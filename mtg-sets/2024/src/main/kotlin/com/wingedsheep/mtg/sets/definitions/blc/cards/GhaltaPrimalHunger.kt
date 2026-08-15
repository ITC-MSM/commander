package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Ghalta, Primal Hunger {10}{G}{G}
 * Legendary Creature — Elder Dinosaur
 * 12/12
 *
 * This spell costs {X} less to cast, where X is the total power of creatures you control.
 * Trample
 */
val GhaltaPrimalHunger = card("Ghalta, Primal Hunger") {
    manaCost = "{10}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Elder Dinosaur"
    power = 12
    toughness = 12
    oracleText = "This spell costs {X} less to cast, where X is the total power of creatures you control.\nTrample"

    keywords(Keyword.TRAMPLE)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(CostReductionSource.TotalPowerYouControl),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "220"
        artist = "Chase Stone"
        imageUri = "https://cards.scryfall.io/normal/front/e/f/ef6a7dc0-35c2-4300-b0c5-28b04706291f.jpg?1721429280"
        flavorText = "The earth walks, strongest of all."
        ruling("2018-01-19", "To determine Ghalta's total cost, start with the mana cost (or an alternative cost if another card's effect allows you to pay one instead), add any cost increases, then apply any cost reductions. Ghalta's mana value remains unchanged, no matter what the total cost to cast it was.")
        ruling("2018-01-19", "The total cost to cast Ghalta is locked in before you pay that cost. For example, if you control three 2/2 creatures, including one you can sacrifice to add {C}, the total cost of Ghalta is {4}{G}{G}. Then you can sacrifice the creature when you activate mana abilities just before paying the cost.")
        ruling("2018-01-19", "If a creature's power is somehow less than 0, it subtracts from the total power of your other creatures. If the total power of your creatures is 0 or less, Ghalta's cost remains {10}{G}{G}.")
        ruling("2018-01-19", "Ghalta's first ability can't reduce its cost below {G}{G}.")
    }
}
