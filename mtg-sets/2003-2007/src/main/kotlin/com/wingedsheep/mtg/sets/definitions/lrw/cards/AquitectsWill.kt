package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddSubtypeEffect
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Aquitect's Will
 * {U}
 * Kindred Sorcery — Merfolk
 *
 * Put a flood counter on target land. That land is an Island in addition to its other types for
 * as long as it has a flood counter on it. If you control a Merfolk, draw a card.
 *
 * The Island half is a **resolution-created continuous effect with no permanent behind it** — the
 * sorcery is in the graveyard by the time the effect matters — so it cannot use the
 * [com.wingedsheep.sdk.scripting.AddLandTypeByCounter] static that Quicksilver Fountain and Eluge
 * carry: that one is a global Layer 4 static borne by a battlefield permanent and stops applying
 * when its source leaves. [Duration.WhileAffectedHasCounter] is the source-independent form of the
 * same gate, watching the counter on the *affected* land rather than on a source, which is exactly
 * what "for as long as it has a flood counter on it" (CR 611.2b) says. Losing the counter ends the
 * effect for good: re-adding one does not resurrect it, matching the rule that a "for as long as"
 * effect whose condition has become false is over rather than suspended.
 *
 * Layer 4 `AddSubtype("Island")` is the same modification the static path emits, so the land gains
 * the intrinsic `{T}: Add {U}` (CR 305.6) and keeps its own land types and abilities.
 *
 * "If you control a Merfolk" is the bare tribal noun, so it reads *permanents* rather than
 * creatures — Lorwyn's Kindred noncreature Merfolk (Merrow Commerce, a resolved Kindred permanent)
 * count, and the check happens on resolution, after the counter is placed.
 */
val AquitectsWill = card("Aquitect's Will") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Kindred Sorcery — Merfolk"
    oracleText = "Put a flood counter on target land. That land is an Island in addition to its " +
        "other types for as long as it has a flood counter on it. If you control a Merfolk, draw a card."

    spell {
        val land = target("target land", Targets.Land)
        effect = Effects.AddCounters(Counters.FLOOD, 1, land)
            .then(
                AddSubtypeEffect(
                    subtype = "Island",
                    target = land,
                    duration = Duration.WhileAffectedHasCounter(Counters.FLOOD)
                )
            )
            .then(
                ConditionalEffect(
                    condition = Conditions.YouControl(GameObjectFilter.Any.withSubtype("Merfolk")),
                    effect = Effects.DrawCards(1)
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Jeff Easley"
        flavorText = "There is nowhere on Lorwyn that the Merrow Lanes cannot go."
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd13d2c1-3db7-4fdc-9321-57c9d6e8c3ae.jpg?1783942906"
    }
}
