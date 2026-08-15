package com.wingedsheep.mtg.sets.definitions.gpt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Izzet Signet
 * {2}
 * Artifact
 * {1}, {T}: Add {U}{R}.
 */
val IzzetSignet = card("Izzet Signet") {
    manaCost = "{2}"
    colorIdentity = "UR"
    typeLine = "Artifact"
    oracleText = "{1}, {T}: Add {U}{R}."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.Composite(Effects.AddMana(Color.BLUE, 1), Effects.AddMana(Color.RED, 1))
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Greg Hildebrandt"
        flavorText = "The Izzet signet is redesigned often, each time becoming closer to a vanity portrait of Niv-Mizzet."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f823be95-bef4-4e86-a924-239be62394bf.jpg?1593272937"
    }
}
