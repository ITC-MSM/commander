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
 * Neglected Manor
 * Land
 *
 * This land enters tapped unless a player has 13 or less life.
 * {T}: Add {W} or {B}.
 */
val NeglectedManor = card("Neglected Manor") {
    typeLine = "Land"
    colorIdentity = "WB"
    oracleText = "This land enters tapped unless a player has 13 or less life.\n{T}: Add {W} or {B}."

    replacementEffect(
        EntersTapped(
            unlessCondition = Conditions.APlayerLifeAtMost(13)
        )
    )

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
        rarity = Rarity.COMMON
        collectorNumber = "264"
        artist = "Carlos Palma Cruchaga"
        flavorText = "They say every family who resided there met a grisly end, and once in a while you'll hear their screams echoing through the moldering halls."
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11cf1531-8a3c-4e28-a114-d3a342b33bb6.jpg?1726286859"
    }
}
