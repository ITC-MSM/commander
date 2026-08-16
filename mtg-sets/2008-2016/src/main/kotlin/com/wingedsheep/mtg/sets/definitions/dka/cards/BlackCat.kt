package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetOpponent

/**
 * Black Cat
 * {1}{B}
 * Creature — Zombie Cat
 * 1/1
 * When this creature dies, target opponent discards a card at random.
 */
val BlackCat = card("Black Cat") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Cat"
    power = 1
    toughness = 1
    oracleText = "When this creature dies, target opponent discards a card at random."

    triggeredAbility {
        trigger = Triggers.Dies
        val opponent = target("target opponent", TargetOpponent())
        effect = Patterns.Hand.discardRandom(1, opponent)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "David Palumbo"
        flavorText = "Its last life is spent tormenting your dreams."
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bb1c6379-69d5-48aa-8d06-257c0592794e.jpg?1783940835"
    }
}
