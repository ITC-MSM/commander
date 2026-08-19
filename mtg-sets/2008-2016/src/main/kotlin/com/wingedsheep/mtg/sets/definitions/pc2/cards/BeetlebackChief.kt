package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Beetleback Chief
 * {2}{R}{R}
 * Creature — Goblin Warrior
 * 2/2
 *
 * When this creature enters, create two 1/1 red Goblin creature tokens.
 */
val BeetlebackChief = card("Beetleback Chief") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    oracleText = "When this creature enters, create two 1/1 red Goblin creature tokens."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Goblin"),
            count = 2,
        )
        description = "When this creature enters, create two 1/1 red Goblin creature tokens."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "40"
        artist = "Wayne England"
        flavorText = "Whether trained, ridden, or eaten, few goblin military innovations have rivaled the bug."
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1e3ccf3d-583c-46b4-b51e-ae1b0628d506.jpg?1783940621"
    }
}
