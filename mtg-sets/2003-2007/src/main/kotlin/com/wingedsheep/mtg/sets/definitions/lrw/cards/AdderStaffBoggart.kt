package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Adder-Staff Boggart
 * {1}{R}
 * Creature — Goblin Warrior
 * 2/1
 * When this creature enters, clash with an opponent. If you win, put a +1/+1 counter on this
 * creature.
 *
 * The plainest of Lorwyn's "clash for a counter" commons (with Oaken Brawler and Paperfin Rascal),
 * and the whole card is `Patterns.Mechanic.clash` — the pattern owns the opponent choice, the
 * public reveal, both top-or-bottom decisions and the "if you win" gate (CR 701.30).
 */
val AdderStaffBoggart = card("Adder-Staff Boggart") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, clash with an opponent. If you win, put a +1/+1 counter on " +
        "this creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.clash(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
        description = "clash with an opponent. If you win, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Jeff Miracola"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d116839f-6a3a-4a2e-a3ab-ea177c012746.jpg?1783942880"
    }
}
