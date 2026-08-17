package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Racers' Ring
 * Land
 * This land enters tapped.
 * {T}: Add {R} or {G}.
 * {2}{R}{G}, {T}, Sacrifice this land: Draw a card.
 */
val RacersRing = card("Racers' Ring") {
    colorIdentity = "GR"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {R} or {G}.\n{2}{R}{G}, {T}, Sacrifice this land: Draw a card."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{R}{G}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "253"
        artist = "Sam White"
        flavorText = "Purchasing chrome racehorses with cash is a common means of dispersing mysterious income."
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a11bdb2-a269-4038-85f3-69fbd02982e9.jpg?1783923055"
    }
}
