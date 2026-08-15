package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Homunculus Horde
 * {3}{U}
 * Creature — Homunculus
 * 2/2
 *
 * Whenever you draw your second card each turn, create a token that's a copy of this creature.
 *
 * The draw trigger uses [Triggers.NthCardDrawn] (n = 2), the shared "your Nth card each turn"
 * detector that fires exactly once per turn when your second card is drawn. The payoff is a copy
 * of this permanent via [Effects.CreateTokenCopyOfSelf]. The token copies the printed
 * characteristics (including this ability), so it can snowball if it too draws a second card.
 */
val HomunculusHorde = card("Homunculus Horde") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Homunculus"
    power = 2
    toughness = 2
    oracleText = "Whenever you draw your second card each turn, create a token that's a copy of this creature."

    triggeredAbility {
        trigger = Triggers.NthCardDrawn(2)
        effect = Effects.CreateTokenCopyOfSelf()
        description = "Whenever you draw your second card each turn, create a token that's a copy of this creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "41"
        artist = "Adrián Rodríguez Pérez"
        flavorText = "\"That is why you triple-check all decimals when commissioning a new batch of homunculi!\"\n—Trigori, Azorius senator"
        imageUri = "https://cards.scryfall.io/normal/front/4/7/470c4d03-340a-4e0e-a59f-f19d05497785.jpg?1782689229"
    }
}
