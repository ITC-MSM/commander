package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Raucous Carnival
 * Land
 *
 * This land enters tapped unless a player has 13 or less life.
 * {T}: Add {R} or {W}.
 */
val RaucousCarnival = card("Raucous Carnival") {
    typeLine = "Land"
    colorIdentity = "RW"
    oracleText = "This land enters tapped unless a player has 13 or less life.\n{T}: Add {R} or {W}."

    replacementEffect(
        EntersTapped(
            unlessCondition = Conditions.APlayerLifeAtMost(13)
        )
    )

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
        rarity = Rarity.COMMON
        collectorNumber = "266"
        artist = "Josu Solano"
        flavorText = "They say that those who enter the amusement park never come back, and their disembodied laughter joins the others on the wind."
        imageUri = "https://cards.scryfall.io/normal/front/3/6/3604a211-9bf7-474e-bd78-32a862f4259c.jpg?1726286867"
    }
}
