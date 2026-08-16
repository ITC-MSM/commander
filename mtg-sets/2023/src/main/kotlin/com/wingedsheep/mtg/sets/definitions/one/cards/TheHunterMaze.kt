package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * The Hunter Maze
 * Land — Sphere
 *
 * This land enters tapped.
 * {T}: Add {G}.
 * {1}{G}, {T}, Sacrifice this land: Draw a card.
 */
val TheHunterMaze = card("The Hunter Maze") {
    colorIdentity = "G"
    typeLine = "Land — Sphere"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {G}.\n" +
        "{1}{G}, {T}, Sacrifice this land: Draw a card."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{G}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "253"
        artist = "Alayna Danner"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/6389c242-2139-4f12-af30-2b080a1c5e83.jpg?1783917981"
    }
}
