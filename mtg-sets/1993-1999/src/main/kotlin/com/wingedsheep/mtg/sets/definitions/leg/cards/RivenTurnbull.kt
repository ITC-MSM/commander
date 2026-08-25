package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Riven Turnbull
 * {5}{U}{B}
 * Legendary Creature — Human Advisor
 * 5/7
 *
 * {T}: Add {B}.
 */
val RivenTurnbull = card("Riven Turnbull") {
    manaCost = "{5}{U}{B}"
    colorIdentity = "BU"
    typeLine = "Legendary Creature — Human Advisor"
    power = 5
    toughness = 7
    oracleText = "{T}: Add {B}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "254"
        artist = "Richard Kane Ferguson"
        flavorText = "\"Political violence is a perfectly legitimate answer to the persecution handed down by " +
            "dignitaries of the state.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d11f90e7-ced1-4d80-8083-99acbf459ad7.jpg?1783948034"
    }
}
