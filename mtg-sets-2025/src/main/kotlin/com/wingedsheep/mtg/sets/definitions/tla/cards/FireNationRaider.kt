package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fire Nation Raider
 * {3}{R}
 * Creature — Human Soldier
 * 4/2
 *
 * Raid — When this creature enters, if you attacked this turn, create a Clue token. (It's an
 * artifact with "{2}, Sacrifice this token: Draw a card.")
 *
 * Raid is modeled as an intervening-if on a [Triggers.EntersBattlefield] triggered ability via
 * [Conditions.YouAttackedThisTurn] (checked both when the trigger would go on the stack and again
 * on resolution, CR 603.4). The reward is the predefined Clue token via [Effects.CreateClue].
 */
val FireNationRaider = card("Fire Nation Raider") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Soldier"
    power = 4
    toughness = 2
    oracleText = "Raid — When this creature enters, if you attacked this turn, create a Clue token. " +
        "(It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.YouAttackedThisTurn
        effect = Effects.CreateClue()
        description = "Raid — When this creature enters, if you attacked this turn, create a Clue token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Tubaki Halsame"
        flavorText = "In the Fire Nation's eyes, the world was theirs and they would let the world know it."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ede1bbdb-c726-46e3-aaf1-b8cf2be2c341.jpg?1764120927"
    }
}
