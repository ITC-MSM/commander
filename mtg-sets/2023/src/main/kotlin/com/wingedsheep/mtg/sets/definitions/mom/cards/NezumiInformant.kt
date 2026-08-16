package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nezumi Informant
 * {1}{B}
 * Creature — Rat Rogue
 * 1/1
 * When this creature enters, each opponent discards a card.
 */
val NezumiInformant = card("Nezumi Informant") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Rat Rogue"
    oracleText = "When this creature enters, each opponent discards a card."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.eachOpponentDiscards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "120"
        artist = "Steve Prescott"
        flavorText = "\"Tell Boss Umezawa there's a new gang moving in on our territory. I'm " +
            "forwarding their insignia. Watch for it elsewhere.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e36fbff8-471e-4cf8-8946-e1e5cdf598af.jpg?1783917003"
        ruling(
            "2023-04-14",
            "To resolve Nezumi Informant's ability in a multiplayer game, the next opponent in " +
                "turn order (or, if it's an opponent's turn, the opponent whose turn it is) " +
                "chooses a card in hand and sets it aside without revealing it. Then each other " +
                "opponent in turn order does the same. Finally, all chosen cards are revealed and " +
                "discarded at the same time."
        )
    }
}
