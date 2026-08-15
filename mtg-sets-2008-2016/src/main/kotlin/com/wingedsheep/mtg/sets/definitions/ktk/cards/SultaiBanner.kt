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
 * Sultai Banner
 * {3}
 * Artifact
 * {T}: Add {B}, {G}, or {U}.
 * {B}{G}{U}, {T}, Sacrifice this artifact: Draw a card.
 */
val SultaiBanner = card("Sultai Banner") {
    manaCost = "{3}"
    colorIdentity = "UBG"
    typeLine = "Artifact"
    oracleText = "{T}: Add {B}, {G}, or {U}.\n{B}{G}{U}, {T}, Sacrifice this artifact: Draw a card."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

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
        cost = Costs.Composite(Costs.Mana("{B}{G}{U}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "225"
        artist = "Daniel Ljunggren"
        flavorText = "\"Power to dominate, cruelty to rule.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/6/1695cf35-8f7c-4674-bfd3-43520b13d084.jpg?1562783039"
    }
}
