package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Brittle Effigy
 * {1}
 * Artifact
 *
 * {4}, {T}, Exile this artifact: Exile target creature.
 *
 * A three-part activation cost — mana, tap, and [Costs.ExileSelf] — over a plain [Effects.Exile] of
 * the chosen creature. The self-exile is a *cost*, so it happens on activation and can't be
 * responded to; the ability still resolves (and still exiles the creature) if the Effigy is already
 * gone from the battlefield by then. The ability is activated from the battlefield like any other
 * permanent ability, so `activateFromZone` stays at its default.
 */
val BrittleEffigy = card("Brittle Effigy") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{4}, {T}, Exile this artifact: Exile target creature."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{4}"),
            Costs.Tap,
            Costs.ExileSelf,
        )
        target = Targets.Creature
        effect = Effects.Exile(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "202"
        artist = "John Avon"
        flavorText = "\"In my early experiments in phylactery, I found that fragile forms have their uses.\"\n" +
            "—Rocati, Duke of Martyne"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1e2a430-339c-46ae-a43f-61e11e018b7b.jpg?1783941792"
    }
}
