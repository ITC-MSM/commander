package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kirol, History Buff // Pack a Punch — Secrets of Strixhaven #198
 * {R}{W} · Legendary Creature — Vampire Cleric · 2/3
 *
 * Whenever one or more cards leave your graveyard, Kirol becomes prepared.
 * (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)
 * //
 * Pack a Punch — {1}{R}{W}, Sorcery: Mill a card. Put two +1/+1 counters on target creature.
 * It gains trample until end of turn.
 *
 * Prepare (Secrets of Strixhaven): Kirol does NOT enter prepared (no PREPARED keyword). He becomes
 * prepared only via his trigger — whenever one or more cards leave his controller's graveyard — through
 * [Effects.BecomePrepared] + the batched [Triggers.CardsLeaveYourGraveyard] trigger. Becoming prepared
 * creates a copy of "Pack a Punch" in exile that its controller may cast for {1}{R}{W}; casting that
 * copy unprepares him. A creature already prepared does not re-prepare, so the trigger is a no-op while
 * he is already prepared. Modeled via [CardLayout.PREPARE] + the `prepare(name) { }` DSL. Pack a Punch
 * composes self-mill ([Patterns.Library.mill]) + two +1/+1 counters + EndOfTurn trample on the target.
 */
val KirolHistoryBuff = card("Kirol, History Buff") {
    manaCost = "{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Vampire Cleric"
    power = 2
    toughness = 3
    oracleText = "Whenever one or more cards leave your graveyard, Kirol becomes prepared. " +
        "(While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    // Whenever one or more cards leave your graveyard, Kirol becomes prepared.
    triggeredAbility {
        trigger = Triggers.CardsLeaveYourGraveyard()
        effect = Effects.BecomePrepared(EffectTarget.Self)
    }

    // Pack a Punch — the prepare spell. Mill a card; two +1/+1 counters + trample on target creature.
    prepare("Pack a Punch") {
        manaCost = "{1}{R}{W}"
        typeLine = "Sorcery"
        oracleText = "Mill a card. Put two +1/+1 counters on target creature. It gains trample until end of turn."
        spell {
            target = Targets.Creature
            effect = Effects.Composite(
                Patterns.Library.mill(1),
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.ContextTarget(0)),
                Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.ContextTarget(0), Duration.EndOfTurn),
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "198"
        artist = "Bryan Sola"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/676ba521-66e4-42cf-a315-70d03cb7334e.jpg?1778165009"
    }
}
