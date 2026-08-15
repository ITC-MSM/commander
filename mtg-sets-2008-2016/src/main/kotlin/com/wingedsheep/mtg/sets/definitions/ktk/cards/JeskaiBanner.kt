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
 * Jeskai Banner
 * {3}
 * Artifact
 * {T}: Add {U}, {R}, or {W}.
 * {U}{R}{W}, {T}, Sacrifice Jeskai Banner: Draw a card.
 */
val JeskaiBanner = card("Jeskai Banner") {
    manaCost = "{3}"
    colorIdentity = "WUR"
    typeLine = "Artifact"
    oracleText = "{T}: Add {U}, {R}, or {W}.\n{U}{R}{W}, {T}, Sacrifice this artifact: Draw a card."

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
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}{R}{W}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "222"
        artist = "Daniel Ljunggren"
        flavorText = "\"Discipline to persevere, insight to discover.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/8/684dc050-a66b-4364-9880-56f383db6c0a.jpg?1562787889"
    }
}
