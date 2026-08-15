package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Storm Fleet Spy
 * {2}{U}
 * Creature — Human Pirate
 * 2/2
 * Raid — When this creature enters, if you attacked this turn, draw a card.
 *
 * Raid (ability word, CR 207.2c — flavor only) is modeled as the intervening-if
 * [Conditions.YouAttackedThisTurn] on the enters trigger (Rule 603.4): the condition is checked both
 * when the trigger would be put on the stack and again as it resolves. "You attacked this turn" reads
 * the controller's [com.wingedsheep.sdk.model.components.PlayerAttackedThisTurnComponent], stamped at
 * declare-attackers and cleared at end of turn.
 */
val StormFleetSpy = card("Storm Fleet Spy") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate"
    power = 2
    toughness = 2
    oracleText = "Raid — When this creature enters, if you attacked this turn, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.YouAttackedThisTurn
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "84"
        artist = "Scott Murphy"
        flavorText = "\"They're searching in the same direction we are. And for the same thing, I'll wager.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f7c33ef4-60bb-4f95-92a5-7abedaac6767.jpg?1782710448"
    }
}
