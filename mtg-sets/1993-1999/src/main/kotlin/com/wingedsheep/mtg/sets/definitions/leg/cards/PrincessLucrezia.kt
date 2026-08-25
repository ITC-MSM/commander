package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Princess Lucrezia
 * {3}{U}{U}{B}
 * Legendary Creature — Human Wizard
 * 5/4
 *
 * {T}: Add {U}.
 */
val PrincessLucrezia = card("Princess Lucrezia") {
    manaCost = "{3}{U}{U}{B}"
    colorIdentity = "BU"
    typeLine = "Legendary Creature — Human Wizard"
    power = 5
    toughness = 4
    oracleText = "{T}: Add {U}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "249"
        artist = "Edward P. Beard, Jr."
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a1dcf48c-2700-4024-807e-9244e4c649ac.jpg?1783948035"
    }
}
