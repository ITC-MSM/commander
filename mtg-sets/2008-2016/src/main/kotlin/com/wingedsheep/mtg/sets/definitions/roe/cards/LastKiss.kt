package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Last Kiss
 * {2}{B}
 * Instant
 *
 * Last Kiss deals 2 damage to target creature and you gain 2 life.
 *
 * Modeling notes:
 *  - Assay compiles the sentence as a `Composite` of `DealDamage` then `GainLife`, which is the
 *    `.then(...)` chain below. The order matters: both halves happen on resolution, and the damage
 *    is dealt first, so the life gain is unconditional even if the damage is prevented or the
 *    creature survives.
 *  - The life gain has no target of its own — Assay emits a bare `GainLife`, and
 *    `EffectTarget.Controller` is already [Effects.GainLife]'s default, so it is left unwritten
 *    rather than restated.
 */
val LastKiss = card("Last Kiss") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Last Kiss deals 2 damage to target creature and you gain 2 life."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(2, creature)
            .then(Effects.GainLife(2))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "116"
        artist = "Vance Kovacs"
        flavorText = "\"Romanticize it, glamorize it, call it what you will. To me, it will always be carnal, bloody murder.\"\n—Ayli, Kamsa cleric"
        imageUri = "https://cards.scryfall.io/normal/front/3/4/348f480f-8f68-4060-b964-f67515930549.jpg?1783941983"
    }
}
