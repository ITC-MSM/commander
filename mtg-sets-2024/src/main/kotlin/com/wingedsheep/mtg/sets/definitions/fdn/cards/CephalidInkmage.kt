package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlocked
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility

/**
 * Cephalid Inkmage
 * {2}{U}
 * Creature — Octopus Wizard
 * 2/2
 * When this creature enters, surveil 3.
 * Threshold — This creature can't be blocked as long as there are seven or more cards in your
 * graveyard.
 *
 * "Threshold" is only an ability word; the mechanic is the [Conditions.CardsInGraveyardAtLeast]
 * gate on a [CantBeBlocked] static, applied during state projection.
 */
val CephalidInkmage = card("Cephalid Inkmage") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Octopus Wizard"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, surveil 3. (Look at the top three cards of your library, " +
        "then put any number of them into your graveyard and the rest on top of your library in any " +
        "order.)\nThreshold — This creature can't be blocked as long as there are seven or more cards " +
        "in your graveyard."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.surveil(3)
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = CantBeBlocked(),
            condition = Conditions.CardsInGraveyardAtLeast(7)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "32"
        artist = "Christopher Burdett"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b7e47680-18c7-4ffb-aac4-c5db6e7095ba.jpg?1782689236"
    }
}
