package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.impending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Effect

/**
 * Overlord of the Floodpits
 * {3}{U}{U}
 * Enchantment Creature — Avatar Horror
 * 5/3
 *
 * Impending 4—{1}{U}{U} (If you cast this spell for its impending cost, it enters with four
 * time counters and isn't a creature until the last is removed. At the beginning of your
 * end step, remove a time counter from it.)
 * Flying
 *
 * Whenever this permanent enters or attacks, draw two cards, then discard a card.
 *
 * Impending is wired by the `impending(n, cost)` DSL helper (CR 702.176): the alternative
 * cost, the "isn't a creature while it has a time counter" type-removing static ability, and
 * the "remove a time counter at the beginning of your end step" trigger. The engine places
 * the four time counters when the spell is cast for its impending cost.
 *
 * The "enters or attacks" ability is one effect (draw two, then discard one) referenced by
 * both the enters-the-battlefield trigger and the attacks trigger.
 */
val OverlordOfTheFloodpits = card("Overlord of the Floodpits") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment Creature — Avatar Horror"
    oracleText = "Impending 4—{1}{U}{U} (If you cast this spell for its impending cost, it enters with four time counters and isn't a creature until the last is removed. At the beginning of your end step, remove a time counter from it.)\n" +
        "Flying\n" +
        "Whenever this permanent enters or attacks, draw two cards, then discard a card."
    power = 5
    toughness = 3

    impending(4, "{1}{U}{U}")

    keywords(Keyword.FLYING)

    // "Draw two cards, then discard a card." Shared by the enters and attacks triggers.
    val drawTwoDiscardOne: Effect = Effects.DrawCards(2).then(Effects.Discard(1))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = drawTwoDiscardOne
        description = "Whenever this permanent enters, draw two cards, then discard a card."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = drawTwoDiscardOne
        description = "Whenever this permanent attacks, draw two cards, then discard a card."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "68"
        artist = "Abz J Harding"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5fca5de8-9cda-4370-9f72-4462f8cfd696.jpg?1726286108"
        ruling("2024-09-20", "If you choose to pay the impending cost rather than the mana cost, you're still casting the spell. It goes on the stack and can be responded to, countered, and so on.")
        ruling("2024-09-20", "If an object enters as a copy of a permanent that was cast with its impending cost, it won't enter with time counters, and it will be a creature.")
    }
}
