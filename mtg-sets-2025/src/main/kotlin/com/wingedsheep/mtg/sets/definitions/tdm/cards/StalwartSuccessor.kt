package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stalwart Successor — Tarkir: Dragonstorm #227
 * {1}{B}{G} · Creature — Human Warrior · 3/2
 *
 * Menace.
 * Whenever one or more counters are put on a creature you control, if it's the first time
 * counters have been put on that creature this turn, put a +1/+1 counter on that creature.
 *
 * Uses [Triggers.countersPlacedOn] — fires on counters of any type ([Counters.ANY]) put on a
 * creature you control, gated by the engine's per-creature "first counters this turn" flag.
 * The reward goes on the triggering creature via [EffectTarget.TriggeringEntity]. The +1/+1
 * counter Stalwart itself adds is *not* the first counter of the turn for that creature (the
 * triggering placement already set the marker), so it does not re-trigger — matching the
 * intervening-if once-per-creature-per-turn behavior.
 */
val StalwartSuccessor = card("Stalwart Successor") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Human Warrior"
    power = 3
    toughness = 2
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Whenever one or more counters are put on a creature you control, if it's the first " +
        "time counters have been put on that creature this turn, put a +1/+1 counter on that " +
        "creature."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.countersPlacedOn()
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.TriggeringEntity)
        description = "Whenever one or more counters are put on a creature you control, if it's " +
            "the first time counters have been put on that creature this turn, put a +1/+1 " +
            "counter on that creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "227"
        artist = "Bastien L. Deharme"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a7b206f-8190-46e6-bb9e-44763d3eb4ac.jpg?1743204896"
    }
}
