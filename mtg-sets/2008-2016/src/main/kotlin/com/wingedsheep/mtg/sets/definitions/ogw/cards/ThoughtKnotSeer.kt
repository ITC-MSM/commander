package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thought-Knot Seer
 * {3}{C}
 * Creature — Eldrazi
 * 4/4
 *
 * When this creature enters, target opponent reveals their hand. You choose a nonland card from
 * it and exile that card.
 * When this creature leaves the battlefield, target opponent draws a card.
 *
 * Modeling notes:
 *  - The ETB is `Patterns.Hand.revealHandAndExileChosen(target = opponent)` — the exact
 *    "reveal hand → choose a nonland → exile it" shape already used by Cruelclaw's Heist / Soul
 *    Search (Thoughtseize with exile instead of discard).
 *  - The LTB is a self `Triggers.LeavesBattlefield` (Goblin Firebug's shape) with its own target
 *    opponent, drawing them a card as the "give it back" cost.
 *  - Canonical printing lives here (Oath of the Gatewatch, its earliest real-expansion printing
 *    per Scryfall — it was never printed in Battle for Zendikar).
 */
val ThoughtKnotSeer = card("Thought-Knot Seer") {
    manaCost = "{3}{C}"
    colorIdentity = ""
    typeLine = "Creature — Eldrazi"
    power = 4
    toughness = 4
    oracleText = "When this creature enters, target opponent reveals their hand. You choose a " +
        "nonland card from it and exile that card.\n" +
        "When this creature leaves the battlefield, target opponent draws a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        effect = Patterns.Hand.revealHandAndExileChosen(target = opponent)
        description = "When this creature enters, target opponent reveals their hand. You " +
            "choose a nonland card from it and exile that card."
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.DrawCards(1, opponent)
        description = "When this creature leaves the battlefield, target opponent draws a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "9"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bffc360e-db41-48f3-9365-680d55046e04.jpg?1783937929"
    }
}
