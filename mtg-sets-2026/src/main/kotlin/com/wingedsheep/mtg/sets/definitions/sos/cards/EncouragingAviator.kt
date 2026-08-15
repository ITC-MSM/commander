package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Encouraging Aviator // Jump — Secrets of Strixhaven #46
 * {2}{U} · Creature — Bird Wizard · 2/3
 *
 * Flying
 * Whenever this creature attacks, it becomes prepared. (While it's prepared, you may cast a copy
 * of its spell. Doing so unprepares it.)
 * //
 * Jump — {U}, Instant: Target creature gains flying until end of turn.
 *
 * Prepare (Secrets of Strixhaven): like Leech Collector, Encouraging Aviator does NOT enter
 * prepared (it has no PREPARED keyword). It only becomes prepared via its attack trigger through
 * [Effects.BecomePrepared]. Becoming prepared creates a copy of its prepare spell ("Jump") in
 * exile that its controller may cast for {U}; casting that copy unprepares the creature. Modeled
 * via [CardLayout.PREPARE] + the `prepare(name) { }` DSL.
 */
val EncouragingAviator = card("Encouraging Aviator") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird Wizard"
    power = 2
    toughness = 3
    oracleText = "Flying\nWhenever this creature attacks, it becomes prepared. (While it's " +
        "prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    keywords(Keyword.FLYING)

    // Whenever this creature attacks, it becomes prepared.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.BecomePrepared(EffectTarget.Self)
    }

    // Jump — the prepare spell. Target creature gains flying until end of turn.
    prepare("Jump") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Target creature gains flying until end of turn."
        spell {
            target = Targets.Creature
            effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.ContextTarget(0))
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "46"
        artist = "Oriana Menendez"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72654b84-9902-41db-92ab-a3499c31221c.jpg?1778165006"
    }
}
