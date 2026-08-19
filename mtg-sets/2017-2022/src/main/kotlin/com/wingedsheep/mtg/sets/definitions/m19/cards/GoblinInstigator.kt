package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Instigator
 * {1}{R}
 * Creature — Goblin Rogue
 * 1/1
 *
 * When this creature enters, create a 1/1 red Goblin creature token.
 */
val GoblinInstigator = card("Goblin Instigator") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    oracleText = "When this creature enters, create a 1/1 red Goblin creature token."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Goblin"),
        )
        description = "When this creature enters, create a 1/1 red Goblin creature token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "Filip Burburan"
        flavorText = "\"We can take 'em. You go first!\""
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90cef94d-d941-4e08-afec-a116626b74fb.jpg?1783934551"
    }
}
