package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Elder Auntie
 * {2}{R}
 * Creature — Goblin Warlock
 * 2/2
 *
 * When this creature enters, create a 1/1 black and red Goblin creature token.
 */
val ElderAuntie = card("Elder Auntie") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warlock"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, create a 1/1 black and red Goblin creature token."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK, Color.RED),
            creatureTypes = setOf("Goblin"),
            count = 1,
            imageUri = "https://cards.scryfall.io/normal/front/6/1/6139a45d-ebc7-4bca-8c13-73c85ea5fe0d.jpg?1768367480"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "133"
        artist = "Caio Monteiro"
        flavorText = "\"Did nephew spit up again? Oh, good! Auntie needed to make a new batch of potions for the merrow market.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/4/84678e98-2258-4ea1-aaf0-8ac4cc2ecf8d.jpg?1767658171"
    }
}
