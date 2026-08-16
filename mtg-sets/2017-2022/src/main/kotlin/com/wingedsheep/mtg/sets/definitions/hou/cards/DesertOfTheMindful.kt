package com.wingedsheep.mtg.sets.definitions.hou.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Desert of the Mindful
 * Land — Desert
 * This land enters tapped.
 * {T}: Add {U}.
 * Cycling {1}{U} ({1}{U}, Discard this card: Draw a card.)
 */
val DesertOfTheMindful = card("Desert of the Mindful") {
    manaCost = ""
    colorIdentity = "U"
    typeLine = "Land — Desert"
    oracleText = "This land enters tapped.\n{T}: Add {U}.\nCycling {1}{U} ({1}{U}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    keywordAbility(KeywordAbility.cycling("{1}{U}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "173"
        artist = "Christine Choi"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/91b92d87-776c-490f-9ff1-234e47145df8.jpg?1783935997"
        ruling(
            "2017-04-18",
            "Desert is a land subtype with no special meaning. It doesn't grant the land an " +
                "intrinsic mana ability. Other cards may care about which lands are Deserts."
        )
    }
}
