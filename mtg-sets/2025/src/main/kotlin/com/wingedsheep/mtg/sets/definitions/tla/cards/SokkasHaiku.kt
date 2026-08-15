package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect

/**
 * Sokka's Haiku
 * {3}{U}{U}
 * Instant — Lesson
 *
 * Counter target spell.
 * Draw a card, then mill three cards.
 * Untap target land.
 */
val SokkasHaiku = card("Sokka's Haiku") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant — Lesson"
    oracleText = "Counter target spell.\n" +
        "Draw a card, then mill three cards.\n" +
        "Untap target land."

    spell {
        // The countered spell must be the first target: CounterEffect (Chosen source)
        // resolves against context.targets.firstOrNull().
        target("counter target spell", Targets.Spell)
        val land = target("untap target land", Targets.Land)
        effect = Effects.CounterSpell() then
            Effects.DrawCards(1) then
            Patterns.Library.mill(3) then
            TapUntapEffect(target = land, tap = false)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "71"
        artist = "Bun Toujo"
        imageUri = "https://cards.scryfall.io/normal/front/0/0/004be4bc-ac3c-4026-8d36-f4687ab18c70.jpg?1764120446"
    }
}
