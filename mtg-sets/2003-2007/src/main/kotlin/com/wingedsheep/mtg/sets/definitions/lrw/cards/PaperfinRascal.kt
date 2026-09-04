package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Paperfin Rascal
 * {2}{U}
 * Creature — Merfolk Rogue
 * 2/2
 * When this creature enters, clash with an opponent. If you win, put a +1/+1 counter on this
 * creature.
 */
val PaperfinRascal = card("Paperfin Rascal") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Rogue"
    power = 2
    toughness = 2
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
        collectorNumber = "77"
        artist = "Zoltan Boros & Gabor Szikszai"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a439c0ca-0cb2-4293-825e-1a72159953b9.jpg?1783942899"
    }
}
