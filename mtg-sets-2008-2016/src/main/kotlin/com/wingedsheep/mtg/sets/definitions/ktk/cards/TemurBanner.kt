package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Temur Banner
 * {3}
 * Artifact
 * {T}: Add {G}, {U}, or {R}.
 * {G}{U}{R}, {T}, Sacrifice Temur Banner: Draw a card.
 */
val TemurBanner = card("Temur Banner") {
    manaCost = "{3}"
    colorIdentity = "URG"
    typeLine = "Artifact"
    oracleText = "{T}: Add {G}, {U}, or {R}.\n{G}{U}{R}, {T}, Sacrifice this artifact: Draw a card."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}{U}{R}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "226"
        artist = "Daniel Ljunggren"
        flavorText = "\"Savagery to survive, courage to triumph.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/9/9990bdff-c27a-447f-b530-d8b7614fe9a0.jpg?1562790835"
    }
}
