package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Faramir, Field Commander
 * {3}{W}
 * Legendary Creature — Human Soldier
 * 3/3
 *
 * At the beginning of your end step, if a creature died under your control this turn, draw a card.
 * Whenever the Ring tempts you, if you chose a creature other than Faramir as your Ring-bearer,
 * create a 1/1 white Human Soldier creature token.
 */
val FaramirFieldCommander = card("Faramir, Field Commander") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Soldier"
    power = 3
    toughness = 3
    oracleText = "At the beginning of your end step, if a creature died under your control this turn, draw a card.\n" +
        "Whenever the Ring tempts you, if you chose a creature other than Faramir as your Ring-bearer, create a 1/1 white Human Soldier creature token."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.ControlledCreatureDiedThisTurn
        effect = Effects.DrawCards(1)
    }

    triggeredAbility {
        trigger = Triggers.RingTemptsYou
        triggerCondition = Conditions.YouChoseOtherCreatureAsRingBearer
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human", "Soldier"),
            imageUri = "https://cards.scryfall.io/normal/front/a/6/a6181330-7521-4ec6-be6c-b35487c2d2d4.jpg?1699974464"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "14"
        artist = "Sidharth Chaturvedi"
        flavorText = "\"A chance for Faramir, Captain of Gondor, to show his quality.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/7/17c2250a-9af1-40de-9f09-7e8c7daec520.jpg?1686967763"
    }
}
