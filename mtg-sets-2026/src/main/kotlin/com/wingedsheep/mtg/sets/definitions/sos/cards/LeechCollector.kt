package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Leech Collector // Bloodletting — Secrets of Strixhaven #88
 * {1}{B} · Creature — Human Warlock · 2/2
 *
 * Whenever you gain life for the first time each turn, this creature becomes prepared.
 * (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)
 * //
 * Bloodletting — {B}, Sorcery: Each opponent loses 2 life.
 *
 * Prepare (Secrets of Strixhaven): unlike "enters prepared" preparation creatures, Leech Collector
 * does NOT enter prepared (it has no PREPARED keyword). It only becomes prepared via its trigger —
 * the first time you gain life each turn — through [Effects.BecomePrepared]. Becoming prepared
 * creates a copy of its prepare spell ("Bloodletting") in exile that its controller may cast for
 * {B}; casting that copy unprepares the creature. Modeled via [CardLayout.PREPARE] + the
 * `prepare(name) { }` DSL.
 */
val LeechCollector = card("Leech Collector") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warlock"
    power = 2
    toughness = 2
    oracleText = "Whenever you gain life for the first time each turn, this creature becomes prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    // Whenever you gain life for the first time each turn, this creature becomes prepared.
    triggeredAbility {
        trigger = Triggers.YouGainLifeFirstTimeEachTurn
        effect = Effects.BecomePrepared(EffectTarget.Self)
    }

    // Bloodletting — the prepare spell. Each opponent loses 2 life.
    prepare("Bloodletting") {
        manaCost = "{B}"
        typeLine = "Sorcery"
        oracleText = "Each opponent loses 2 life."
        spell {
            effect = Effects.LoseLife(2, EffectTarget.PlayerRef(Player.EachOpponent))
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "88"
        artist = "Chris Rallis"
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c715fe4c-c0e7-4342-811f-b74687851097.jpg?1775937525"
    }
}
