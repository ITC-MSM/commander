package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Stone Quarry
 * Land
 * This land enters tapped.
 * {T}: Add {R} or {W}.
 */
val StoneQuarry = card("Stone Quarry") {
    colorIdentity = "RW"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {R} or {W}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "279"
        artist = "Cliff Childs"
        flavorText = "In Gavony, headstones come only from quarries that have been blessed by chaplains."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e636cdc3-4b83-4f15-ad4a-6c8fa3533408.jpg"
    }
}
