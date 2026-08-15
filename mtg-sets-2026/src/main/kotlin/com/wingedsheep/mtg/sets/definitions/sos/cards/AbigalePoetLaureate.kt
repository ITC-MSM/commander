package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Abigale, Poet Laureate // Heroic Stanza — Secrets of Strixhaven #170
 * {1}{W}{B} · Legendary Creature — Bird Bard · 2/3
 *
 * Flying
 * Whenever you cast a creature spell, Abigale becomes prepared.
 * (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)
 * //
 * Heroic Stanza — {1}{W/B}, Sorcery: Put a +1/+1 counter on target creature.
 *
 * Prepare (Secrets of Strixhaven): unlike "enters prepared" preparation creatures, Abigale does NOT
 * enter prepared (no PREPARED keyword). She becomes prepared only via her trigger — whenever you cast
 * a creature spell — through [Effects.BecomePrepared]. Becoming prepared creates a copy of her prepare
 * spell ("Heroic Stanza") in exile that her controller may cast for {1}{W/B}; casting that copy
 * unprepares her. A creature already prepared does not re-prepare, so the trigger is a no-op while she
 * is already prepared. Modeled via [CardLayout.PREPARE] + the `prepare(name) { }` DSL.
 */
val AbigalePoetLaureate = card("Abigale, Poet Laureate") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Bird Bard"
    power = 2
    toughness = 3
    oracleText = "Flying\n" +
        "Whenever you cast a creature spell, Abigale becomes prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    keywords(Keyword.FLYING)

    // Whenever you cast a creature spell, Abigale becomes prepared.
    triggeredAbility {
        trigger = Triggers.YouCastCreature
        effect = Effects.BecomePrepared(EffectTarget.Self)
    }

    // Heroic Stanza — the prepare spell. Put a +1/+1 counter on target creature.
    prepare("Heroic Stanza") {
        manaCost = "{1}{W/B}"
        typeLine = "Sorcery"
        oracleText = "Put a +1/+1 counter on target creature."
        spell {
            target = Targets.Creature
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0))
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "170"
        artist = "Olivier Bernard"
        imageUri = "https://cards.scryfall.io/normal/front/7/7/77285d12-e658-4eb3-ba13-ff202afab9c8.jpg?1778164962"
    }
}
