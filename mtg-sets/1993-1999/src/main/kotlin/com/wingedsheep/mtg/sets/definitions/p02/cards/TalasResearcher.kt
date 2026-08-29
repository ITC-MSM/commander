package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction

/**
 * Talas Researcher
 * {4}{U}
 * Creature — Human Pirate Wizard
 * 1/1
 *
 * {T}: Draw a card. Activate only during your turn, before attackers are declared.
 */
val TalasResearcher = card("Talas Researcher") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate Wizard"
    oracleText = "{T}: Draw a card. Activate only during your turn, before attackers are declared."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Tap
        restrictions = listOf(
            ActivationRestriction.OnlyDuringYourTurn,
            ActivationRestriction.BeforeStep(Step.DECLARE_ATTACKERS)
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "51"
        artist = "Kaja Foglio"
        flavorText = "From time, knowledge.\nFrom knowledge, power."
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1c51008b-7031-4dbc-b4d6-7433c05bf6dc.jpg"
    }
}
