package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/** Pipeline slot holding the destroyed creature's toughness, frozen before the destruction. */
private const val STRANGLED_TOUGHNESS = "strangledToughness"

/**
 * Weed Strangle
 * {3}{B}{B}
 * Sorcery
 *
 * Destroy target creature. Clash with an opponent. If you win, you gain life equal to that
 * creature's toughness.
 *
 * The life gain reads a creature that the spell's own earlier step already destroyed, so the
 * toughness has to be **frozen before the move**: [com.wingedsheep.sdk.scripting.values.EntityReference.Target]
 * is deliberately `LIVE_ONLY` (a departed target reads as absent, CR 608.2b), and there is no
 * target LKI snapshot to fall back on. [Effects.StoreNumber] evaluates
 * [DynamicAmounts.targetToughness] once, up front, and the win rider reads it back through
 * [DynamicAmount.VariableReference]. That also matches the printed intent — the creature's
 * toughness as it last existed on the battlefield — and stays correct for an indestructible or
 * regenerated target, which is still on the board but whose toughness the freeze already captured.
 *
 * Freezing *before* the clash rather than inside the win branch matters for a second reason: the
 * clash pauses twice for the two top-or-bottom decisions, and the read happens on the far side of
 * both, inside the gate's `then`. The store therefore has to survive a gated pause, which is
 * exactly what `WeedStrangleScenarioTest` forces with two clash-legal libraries.
 */
val WeedStrangle = card("Weed Strangle") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature. Clash with an opponent. If you win, you gain life equal " +
        "to that creature's toughness. (Each clashing player reveals the top card of their library, " +
        "then puts that card on their choice of the top or bottom. A player wins if their card had " +
        "a greater mana value.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.StoreNumber(STRANGLED_TOUGHNESS, DynamicAmounts.targetToughness())
            .then(Effects.Destroy(creature))
            .then(
                Patterns.Mechanic.clash(
                    Effects.GainLife(DynamicAmount.VariableReference(STRANGLED_TOUGHNESS))
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "Jesper Ejsing"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1f7fb79-19a8-483a-bf91-e687f7da4e9c.jpg?1783942880"
    }
}
