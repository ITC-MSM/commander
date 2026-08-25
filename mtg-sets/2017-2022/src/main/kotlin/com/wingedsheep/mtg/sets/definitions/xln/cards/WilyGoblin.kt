package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wily Goblin
 * {R}{R}
 * Creature — Goblin Pirate
 * 1/1
 *
 * When this creature enters, create a Treasure token.
 */
val WilyGoblin = card("Wily Goblin") {
    manaCost = "{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Pirate"
    oracleText = "When this creature enters, create a Treasure token. (It's an artifact with " +
        "\"{T}, Sacrifice this token: Add one mana of any color.\")"
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateTreasure()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Steve Prescott"
        flavorText = "Goblins climb and swing with ease, whether through a pirate ship's rigging or a tree's branches."
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a175b24c-c256-45a8-b24a-6b83e42d5efa.jpg"
    }
}
