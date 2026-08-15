package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Station Monitor
 * {W}{U}
 * Creature — Lizard Artificer
 * 2/2
 *
 * Whenever you cast your second spell each turn, create a 1/1 colorless Drone artifact creature
 * token with flying and "This token can block only creatures with flying."
 */
val StationMonitor = card("Station Monitor") {
    manaCost = "{W}{U}"
    colorIdentity = "WU"
    typeLine = "Creature — Lizard Artificer"
    power = 2
    toughness = 2
    oracleText = "Whenever you cast your second spell each turn, create a 1/1 colorless Drone artifact " +
        "creature token with flying and \"This token can block only creatures with flying.\""

    triggeredAbility {
        trigger = Triggers.NthSpellCast(2, Player.You)
        effect = Effects.CreateDroneToken()
        description = "create a 1/1 colorless Drone artifact creature token with flying and " +
            "\"This token can block only creatures with flying.\""
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "230"
        artist = "Camille Alquier"
        flavorText = "\"Keeping tabs on a whole station is too much for just one.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba9f6d16-ee3e-4fbb-b78a-6292188eb61f.jpg?1752947501"

        ruling(
            "2025-07-25",
            "Station Monitor's ability will count any spells you've cast this turn, which may include " +
                "Station Monitor itself. It doesn't matter if the other spells resolved, didn't resolve, " +
                "were countered, or are still on the stack. If Station Monitor was the first spell you " +
                "cast this turn, the next spell you cast this turn is your second spell."
        )
        ruling(
            "2025-07-25",
            "Station Monitor's ability resolves before the spell that caused it to trigger. It resolves " +
                "even if that spell is countered or otherwise leaves the stack without resolving."
        )
    }
}
