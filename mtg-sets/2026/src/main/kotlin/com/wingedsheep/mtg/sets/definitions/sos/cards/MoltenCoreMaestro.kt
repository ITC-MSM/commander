package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.opus
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Molten-Core Maestro
 * {1}{R}
 * Creature — Goblin Bard
 * 2/2
 * Menace
 * Opus — Whenever you cast an instant or sorcery spell, put a +1/+1 counter on this creature.
 * If five or more mana was spent to cast that spell, add an amount of {R} equal to this
 * creature's power.
 *
 * "Opus" is an ability word (flavor only). The `opus { }` builder wires the spell-cast trigger
 * and the 5+ mana tier (`ContextProperty(MANA_SPENT_ON_TRIGGERING_SPELL) >= 5`). The mana
 * production is `alsoIfFiveOrMore`, so it runs in addition to the unconditional +1/+1 counter.
 * The {R} amount reads the creature's (already-incremented) power via [DynamicAmounts.sourcePower].
 */
val MoltenCoreMaestro = card("Molten-Core Maestro") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Bard"
    power = 2
    toughness = 2
    oracleText = "Menace\nOpus — Whenever you cast an instant or sorcery spell, put a +1/+1 " +
        "counter on this creature. If five or more mana was spent to cast that spell, add an " +
        "amount of {R} equal to this creature's power."
    keywords(Keyword.MENACE)

    opus {
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        alsoIfFiveOrMore = Effects.AddMana(Color.RED, DynamicAmounts.sourcePower())
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "125"
        artist = "Aleksi Briclot"
        flavorText = "\"My media? Fury and flame.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/2/326dfe32-3674-4a11-acd8-5ba62371235a.jpg?1775937832"
    }
}
