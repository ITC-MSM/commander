package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Germinate Recruits — X reads the LIFE_GAINED turn tracker for the caster
 * ([DynamicAmounts.lifeGainedThisTurn]); with no life gained this turn it creates nothing.
 * The dynamic-count `Effects.CreateToken` overload has no `name` parameter, so the named
 * Cadet token is built from [CreateTokenEffect] directly (colorless: no colors).
 */
val GerminateRecruits = card("Germinate Recruits") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Create X 2/2 colorless Wizard Soldier creature tokens named Cadet, where X is the amount of life you gained this turn."

    spell {
        effect = CreateTokenEffect(
            count = DynamicAmounts.lifeGainedThisTurn(),
            power = 2,
            toughness = 2,
            colors = emptySet(),
            creatureTypes = setOf("Wizard", "Soldier"),
            name = "Cadet",
            imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318",
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "9"
        artist = "Rovina Cai"
        flavorText = "\"Bring me the underperformers. I will make them flourish.\"\n—Kwia Vigorbloom"
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b5d7d19-b32a-4786-ae9a-00da5e6658ad.jpg?1789644810"
        inBooster = false
    }
}
