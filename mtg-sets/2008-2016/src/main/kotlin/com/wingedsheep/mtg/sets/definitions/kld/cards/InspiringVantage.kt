package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Inspiring Vantage
 * Land
 *
 * This land enters tapped unless you control two or fewer other lands.
 * {T}: Add {R} or {W}.
 */
val InspiringVantage = card("Inspiring Vantage") {
    typeLine = "Land"
    colorIdentity = "RW"
    oracleText = "This land enters tapped unless you control two or fewer other lands.\n{T}: Add {R} or {W}."

    replacementEffect(EntersTapped(
        unlessCondition = Conditions.YouControlOtherAtMost(2, GameObjectFilter.Land)
    ))

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "246"
        artist = "Jonas De Ro"
        imageUri = "https://cards.scryfall.io/normal/front/1/6/160ac412-005f-48ca-a204-10207307c6c2.jpg?1783937144"
        ruling("2016-09-20", "If one of these lands is your first, second, or third land, it enters the battlefield untapped. If you control three or more other lands, however, it enters the battlefield tapped.")
        ruling("2016-09-20", "If one of these lands enters the battlefield at the same time as one or more other lands (due to Oblivion Sower or Warp World, perhaps), it doesn't take those lands into consideration when determining how many other lands you control.")
    }
}
