package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Abstract Paintmage — Secrets of Strixhaven #171
 * {U}{U/R}{R} · Creature — Djinn Sorcerer · 2/2
 *
 * At the beginning of your first main phase, add {U}{R}. Spend this mana only to cast instant and
 * sorcery spells.
 *
 * Modeled as a [Triggers.FirstMainPhase] triggered ability that adds one {U} and one {R}, both
 * carrying [ManaRestriction.InstantOrSorceryOnly] (the engine's "spend only to cast instant or
 * sorcery spells" restriction).
 */
val AbstractPaintmage = card("Abstract Paintmage") {
    manaCost = "{U}{U/R}{R}"
    colorIdentity = "UR"
    typeLine = "Creature — Djinn Sorcerer"
    power = 2
    toughness = 2
    oracleText = "At the beginning of your first main phase, add {U}{R}. Spend this mana only to cast instant and sorcery spells."

    triggeredAbility {
        trigger = Triggers.FirstMainPhase
        effect = Effects.Composite(
            Effects.AddMana(Color.BLUE, 1, ManaRestriction.InstantOrSorceryOnly),
            Effects.AddMana(Color.RED, 1, ManaRestriction.InstantOrSorceryOnly)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "171"
        artist = "David Auden Nash"
        flavorText = "\"Life is vivid; the pigments I use should be too.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea008094-d995-4740-9b39-c61049356c55.jpg?1775938173"
    }
}
