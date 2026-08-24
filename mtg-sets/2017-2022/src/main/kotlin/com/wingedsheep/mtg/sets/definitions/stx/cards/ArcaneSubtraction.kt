package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Arcane Subtraction — Strixhaven: School of Mages #36 (canonical printing)
 * {1}{U} · Instant
 *
 * Target creature gets -4/-0 until end of turn.
 * Learn.
 *
 * A combat trick that blanks an attacker's damage rather than killing it — -0 toughness means it
 * never dies to this, so no dies-triggers and no last-known-information subtleties. Power can go
 * negative, which the engine floors at 0 for damage purposes (CR 107.1b).
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val ArcaneSubtraction = card("Arcane Subtraction") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature gets -4/-0 until end of turn.\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-4, 0, creature) then Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Anastasia Ovchinnikova"
        flavorText = "The class learned little that day."
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68cdb00c-ea86-4b72-8f62-570820edfa1b.jpg?1783927381"
    }
}
