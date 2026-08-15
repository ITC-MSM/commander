package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect

/**
 * Starting Town
 * Land — Town
 *
 * This land enters tapped unless it's your first, second, or third turn of the game.
 * {T}: Add {C}.
 * {T}, Pay 1 life: Add one mana of any color.
 */
val StartingTown = card("Starting Town") {
    typeLine = "Land — Town"
    colorIdentity = ""
    oracleText = "This land enters tapped unless it's your first, second, or third turn of the game.\n{T}: Add {C}.\n{T}, Pay 1 life: Add one mana of any color."

    replacementEffect(
        EntersTapped(
            unlessCondition = Conditions.ControllerTurnsTakenAtMost(3)
        )
    )

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddColorlessManaEffect(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "289"
        artist = "Hristo D. Chukov"
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc7d1912-7e27-49ef-bd98-375d975a42b0.jpg?1748706861"
    }
}
