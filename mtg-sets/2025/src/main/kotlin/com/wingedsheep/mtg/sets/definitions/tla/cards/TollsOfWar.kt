package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tolls of War
 * {W}{B}
 * Enchantment
 *
 * When this enchantment enters, create a Clue token. (It's an artifact with
 * "{2}, Sacrifice this token: Draw a card.")
 * Whenever you sacrifice a permanent during your turn, create a 1/1 white Ally
 * creature token. This ability triggers only once each turn.
 */
val TollsOfWar = card("Tolls of War") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, create a Clue token. " +
        "(It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")\n" +
        "Whenever you sacrifice a permanent during your turn, create a 1/1 white Ally " +
        "creature token. This ability triggers only once each turn."

    // When this enchantment enters, create a Clue token.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateClue()
    }

    // Whenever you sacrifice a permanent during your turn, create a 1/1 white Ally
    // creature token. This ability triggers only once each turn.
    triggeredAbility {
        trigger = Triggers.YouSacrificeOneOrMore()
        triggerCondition = Conditions.IsYourTurn
        oncePerTurn = true
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Ally")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "245"
        artist = "Jocelin Carmes"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d822b9ad-a787-4355-88c2-07ae2bd3a78e.jpg?1764121807"
    }
}
