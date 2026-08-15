package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Coastal Tower
 * Land
 * This land enters tapped.
 * {T}: Add {W} or {U}.
 */
val CoastalTower = card("Coastal Tower") {
    typeLine = "Land"
    colorIdentity = "WU"
    oracleText = "This land enters tapped.\n{T}: Add {W} or {U}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "321"
        artist = "Don Hazeltine"
        flavorText = "The Capashen built the highest towers in Benalia to afford themselves the best view."
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d115dbff-e35b-495f-a1e3-19651895927e.jpg?1562937019"
    }
}
