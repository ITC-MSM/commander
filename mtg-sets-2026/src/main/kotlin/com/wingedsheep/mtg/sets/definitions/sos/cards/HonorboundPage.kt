package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Honorbound Page // Forum's Favor — Secrets of Strixhaven #19
 * {3}{W} · Creature — Cat Cleric · 3/3
 *
 * First strike
 * This creature enters prepared. (While it's prepared, you may cast a copy of its spell.
 * Doing so unprepares it.)
 * //
 * Forum's Favor — {W}, Sorcery: Target creature gets +1/+0 and gains flying until end of turn.
 *
 * Prepare (Secrets of Strixhaven): the creature enters with the PREPARED keyword. Becoming
 * prepared creates a copy of its prepare spell ("Forum's Favor") in exile that its controller may
 * cast for {W}; casting that copy unprepares the creature. Modeled via [CardLayout.PREPARE] +
 * the `prepare(name) { }` DSL. The +1/+0 buff and granted flying are both EndOfTurn.
 */
val HonorboundPage = card("Honorbound Page") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Cleric"
    power = 3
    toughness = 3
    oracleText = "First strike\n" +
        "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    keywords(Keyword.FIRST_STRIKE, Keyword.PREPARED)

    // Forum's Favor — the prepare spell. Target creature gets +1/+0 and gains flying until end of turn.
    prepare("Forum's Favor") {
        manaCost = "{W}"
        typeLine = "Sorcery"
        oracleText = "Target creature gets +1/+0 and gains flying until end of turn."
        spell {
            target = Targets.Creature
            effect = Effects.Composite(
                Effects.ModifyStats(1, 0, EffectTarget.ContextTarget(0)),
                Effects.GrantKeyword(Keyword.FLYING, EffectTarget.ContextTarget(0), Duration.EndOfTurn),
            )
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/7/9/79a70863-860f-4a7b-9cb2-d3546b689d44.jpg?1775937039"
    }
}
