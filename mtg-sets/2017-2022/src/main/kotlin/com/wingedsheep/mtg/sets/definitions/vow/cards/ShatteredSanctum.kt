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
 * Shattered Sanctum
 * Land
 *
 * This land enters tapped unless you control two or more other lands.
 * {T}: Add {W} or {B}.
 *
 * One of the "slow lands" — enters untapped only once you already control at least
 * two other lands. The [GameObjectFilter.Land] aggregate counts the entering land
 * itself, so "two or more *other* lands" is "three or more lands total", i.e. the
 * untapped condition is `controlled lands >= 3`.
 */
val ShatteredSanctum = card("Shattered Sanctum") {
    typeLine = "Land"
    colorIdentity = "WB"
    oracleText = "This land enters tapped unless you control two or more other lands.\n{T}: Add {W} or {B}."

    replacementEffect(EntersTapped(
        unlessCondition = Compare(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Land),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(3)
        )
    ))

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "264"
        artist = "Muhammad Firdaus"
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad44c9aa-eb8f-4200-8dfe-2af728d80083.jpg?1655879804"
        flavorText = "The Quietus Cult holds their grim ceremonies in the dark places where unburied dead litter the ground."
        ruling("2021-11-19", "If one of these lands enters the battlefield at the same time as one or more other lands, it doesn't take those lands into consideration when determining how many other lands you control.")
    }
}
