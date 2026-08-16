package com.wingedsheep.mtg.sets.definitions.hob.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry

/**
 * Gundabad Opportunist
 * {3}{R}
 * Creature — Goblin Rogue
 * 4/2
 * When this creature enters, exile the top card of your library. Until the end of your next turn,
 * you may play that card.
 *
 * The standard impulse-draw pipeline (cf. Alania's Pathmaker), which the SDK publishes whole as
 * [Patterns.Exile.impulse]: gather the top card, move it to exile, then grant the permission bounded
 * by [MayPlayExpiry.UntilEndOfNextTurn]. The permission is "play", so a land exiled this way still
 * costs a land drop.
 */
val GundabadOpportunist = card("Gundabad Opportunist") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    oracleText = "When this creature enters, exile the top card of your library. Until the end of your next turn, you may play that card."
    power = 4
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Exile.impulse(count = 1, expiry = MayPlayExpiry.UntilEndOfNextTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "101"
        artist = "Michele Giorgi"
        flavorText = "Goblins had a simple approach to ownership: whatever you could hold onto was yours."
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc4a60b8-a5bb-4dbf-8d48-95caf757eac3.jpg?1785497118"
    }
}
