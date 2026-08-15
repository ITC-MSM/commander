package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Memorial to Glory
 * Land
 * Memorial to Glory enters the battlefield tapped.
 * {T}: Add {W}.
 * {3}{W}, {T}, Sacrifice Memorial to Glory: Create two 1/1 white Soldier creature tokens.
 */
val MemorialToGlory = card("Memorial to Glory") {
    typeLine = "Land"
    colorIdentity = "W"
    oracleText = "Memorial to Glory enters the battlefield tapped.\n{T}: Add {W}.\n{3}{W}, {T}, Sacrifice Memorial to Glory: Create two 1/1 white Soldier creature tokens."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{W}"), Costs.Tap, Costs.SacrificeSelf)
        effect = CreateTokenEffect(
            count = 2,
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Soldier"),
            keywords = emptySet(),
            imageUri = "https://cards.scryfall.io/normal/front/8/c/8c4b0257-2ca5-4015-9d63-d7cf6e87ab9d.jpg?1675456186"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "244"
        artist = "James Paick"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/564d6ff1-2be1-42f1-a919-fe8c2e148c3f.jpg?1562735942"
    }
}
