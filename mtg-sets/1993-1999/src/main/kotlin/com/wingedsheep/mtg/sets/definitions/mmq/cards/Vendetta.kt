package com.wingedsheep.mtg.sets.definitions.mmq.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Vendetta
 * {B}
 * Instant
 *
 * Destroy target nonblack creature. It can't be regenerated. You lose life equal to that creature's toughness.
 *
 * Modeling notes:
 *  - "nonblack" is a restriction on what may be chosen, so it belongs on the target requirement,
 *    not on a condition: `TargetFilter.Creature.notColor(Color.BLACK)` — the same spelling Notorious
 *    Assassin uses in this set, and what Assay compiles (`IsCreature` + `NotColor BLACK`).
 *  - "It can't be regenerated" is a marker placed on the creature *before* the destroy, matching
 *    Assay's ordering (`CantBeRegenerated`, then the `byDestruction` move to the graveyard). It is
 *    written out rather than folded into `Effects.Destroy(noRegenerate = true)` only so the life
 *    loss can be slotted between the two — see the next note.
 *  - "You lose life equal to that creature's toughness" reads last-known information: by the time
 *    the sentence resolves the creature is gone. The engine's `DynamicAmount.EntityProperty` has no
 *    last-known fallback for a *spell's* target (only for the triggering entity and the source), so
 *    evaluated after the move it would read the creature's printed toughness and miss counters,
 *    Auras and lords. Reading it while the creature is still on the battlefield — the life loss
 *    ordered before the destroy — yields the rules-correct last-known value in every case,
 *    indestructible creatures included. Agonizing Demise lowers its power-reading rider the same way
 *    and for the same reason.
 *  - The life loss is not targeted; it hits the spell's controller, so `EffectTarget.Controller` is
 *    passed explicitly — [Effects.LoseLife] defaults to the *target opponent*, so here the default
 *    is the wrong player and has to be overridden.
 */
val Vendetta = card("Vendetta") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target nonblack creature. It can't be regenerated. You lose life equal to that creature's toughness."

    spell {
        val creature = target(
            "target nonblack creature",
            TargetCreature(filter = TargetFilter.Creature.notColor(Color.BLACK))
        )
        effect = Effects.CantBeRegenerated(creature)
            .then(Effects.LoseLife(DynamicAmounts.targetToughness(0), EffectTarget.Controller))
            .then(Effects.Destroy(creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Dan Frazier"
        flavorText = "Starke knew the voice was Takara's, but the venom was Volrath's."
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67ced38e-0f33-4bda-8e18-09f6ac03a3d7.jpg?1783945943"
    }
}
