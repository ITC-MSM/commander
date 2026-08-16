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
 * Concealed Courtyard
 * Land
 *
 * This land enters tapped unless you control two or fewer other lands.
 * {T}: Add {W} or {B}.
 */
val ConcealedCourtyard = card("Concealed Courtyard") {
    typeLine = "Land"
    colorIdentity = "WB"
    oracleText = "This land enters tapped unless you control two or fewer other lands.\n{T}: Add {W} or {B}."

    replacementEffect(EntersTapped(
        unlessCondition = Conditions.YouControlOtherAtMost(2, GameObjectFilter.Land)
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
        collectorNumber = "245"
        artist = "Jung Park"
        imageUri = "https://cards.scryfall.io/normal/front/c/8/c8769e97-aee8-4466-a9d7-0f4245ae4a97.jpg?1783937145"
        ruling("2016-09-20", "If one of these lands is your first, second, or third land, it enters the battlefield untapped. If you control three or more other lands, however, it enters the battlefield tapped.")
        ruling("2016-09-20", "If one of these lands enters the battlefield at the same time as one or more other lands (due to Oblivion Sower or Warp World, perhaps), it doesn't take those lands into consideration when determining how many other lands you control.")
    }
}
