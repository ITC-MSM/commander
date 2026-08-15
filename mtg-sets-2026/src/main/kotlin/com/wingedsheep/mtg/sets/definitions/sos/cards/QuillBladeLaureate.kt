package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Quill-Blade Laureate // Twofold Intent — Secrets of Strixhaven #27
 * {1}{W} · Creature — Human Cleric · 1/1
 *
 * Double strike
 * This creature enters prepared. (While it's prepared, you may cast a copy of its spell.
 * Doing so unprepares it.)
 * //
 * Twofold Intent — {1}{W}, Sorcery: Target creature gets +1/+0 and gains double strike until end of turn.
 *
 * Prepare (Secrets of Strixhaven): the creature enters with the PREPARED keyword. Becoming
 * prepared creates a copy of its prepare spell ("Twofold Intent") in exile that its controller may
 * cast for {1}{W}; casting that copy unprepares the creature. Modeled via [CardLayout.PREPARE] +
 * the `prepare(name) { }` DSL. The +1/+0 buff and granted double strike are both EndOfTurn.
 */
val QuillBladeLaureate = card("Quill-Blade Laureate") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "Double strike\n" +
        "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    keywords(Keyword.DOUBLE_STRIKE, Keyword.PREPARED)

    // Twofold Intent — the prepare spell. Target creature gets +1/+0 and gains double strike until end of turn.
    prepare("Twofold Intent") {
        manaCost = "{1}{W}"
        typeLine = "Sorcery"
        oracleText = "Target creature gets +1/+0 and gains double strike until end of turn."
        spell {
            target = Targets.Creature
            effect = Effects.Composite(
                Effects.ModifyStats(1, 0, EffectTarget.ContextTarget(0)),
                Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, EffectTarget.ContextTarget(0), Duration.EndOfTurn),
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "27"
        artist = "Elizabeth Peiró"
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62a47835-5719-48c4-a740-a0c5f00dce11.jpg?1775937102"
    }
}
