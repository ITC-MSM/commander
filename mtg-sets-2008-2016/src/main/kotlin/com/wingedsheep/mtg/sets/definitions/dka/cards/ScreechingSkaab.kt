package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Screeching Skaab
 * {1}{U}
 * Creature — Zombie
 * 2/1
 * When this creature enters, mill two cards.
 */
val ScreechingSkaab = card("Screeching Skaab") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie"
    oracleText = "When this creature enters, mill two cards."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.mill(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "47"
        artist = "Clint Cearley"
        flavorText = "Its screeching is the sound of you losing your mind."
        imageUri =
            "https://cards.scryfall.io/normal/front/3/c/3c40a2c7-df7a-41a6-a49e-5f7db808b810.jpg?1783940837"
    }
}
