package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Stormcarved Coast
 * Land
 *
 * This land enters tapped unless you control two or more other lands.
 * {T}: Add {U} or {R}.
 *
 * One of the "slow lands" — enters untapped only once you already control at least
 * two other lands. The [GameObjectFilter.Land] aggregate counts the entering land
 * itself, so "two or more *other* lands" is "three or more lands total", i.e. the
 * untapped condition is `controlled lands >= 3`.
 */
val StormcarvedCoast = card("Stormcarved Coast") {
    typeLine = "Land"
    colorIdentity = "UR"
    oracleText = "This land enters tapped unless you control two or more other lands.\n{T}: Add {U} or {R}."

    replacementEffect(EntersTapped(
        unlessCondition = Compare(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Land),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(3)
        )
    ))

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "265"
        artist = "Sarah Finnigan"
        imageUri = "https://cards.scryfall.io/normal/front/2/9/299f1dee-b3d7-472b-aa0b-2f9b46a96da5.jpg?1655879810"
        flavorText = "Frequent, violent storms shape the Nephalia coastline, sculpting cliffs and caves where monsters often lurk."
        ruling("2021-11-19", "If one of these lands enters the battlefield at the same time as one or more other lands, it doesn't take those lands into consideration when determining how many other lands you control.")
    }
}
