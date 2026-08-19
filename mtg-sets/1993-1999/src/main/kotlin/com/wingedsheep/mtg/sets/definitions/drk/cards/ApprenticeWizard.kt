package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Apprentice Wizard
 * {1}{U}{U}
 * Creature — Human Wizard
 * 0/1
 * {U}, {T}: Add {C}{C}{C}.
 */
val ApprenticeWizard = card("Apprentice Wizard") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 0
    toughness = 1
    oracleText = "{U}, {T}: Add {C}{C}{C}."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap)
        effect = Effects.AddColorlessMana(3)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "21"
        artist = "Dan Frazier"
        imageUri = "https://cards.scryfall.io/normal/front/1/5/151b332e-164b-4646-8f52-741984cd71ad.jpg?1783947945"
    }
}
