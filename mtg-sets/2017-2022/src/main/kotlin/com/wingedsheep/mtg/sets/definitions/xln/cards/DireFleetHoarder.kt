package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dire Fleet Hoarder
 * {1}{B}
 * Creature — Human Pirate
 * 2/1
 *
 * When this creature dies, create a Treasure token.
 */
val DireFleetHoarder = card("Dire Fleet Hoarder") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Pirate"
    oracleText = "When this creature dies, create a Treasure token. (It's an artifact with " +
        "\"{T}, Sacrifice this token: Add one mana of any color.\")"
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateTreasure()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Deruchenko Alexander"
        flavorText = "Among the pirates of the Brazen Coalition, the only thing more dangerous than failure is success."
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fcd719e-fa21-42bb-968b-ccc0cd3829c6.jpg"
    }
}
