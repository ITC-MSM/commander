package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Abuelo's Awakening
 * {X}{3}{W}
 * Sorcery — Rare — The Lost Caverns of Ixalan #1
 *
 * "Return target artifact or non-Aura enchantment card from your graveyard to the battlefield with
 *  X additional +1/+1 counters on it. It's a 1/1 Spirit creature with flying in addition to its
 *  other types."
 *
 * Resolution (CR 608.2) — a sequential composite, all as part of the spell resolving:
 *  1. [Effects.PutOntoBattlefield] moves the targeted card from the controller's graveyard to the
 *     battlefield. The target reference [t] is a `BoundVariable` whose entity ID is stable across the
 *     zone change (same pattern as Defossilize), so every later effect identifies the same permanent.
 *  2. [Effects.BecomeCreature] makes it a **1/1** creature with **flying**, permanently
 *     (`Duration.Permanent`). BecomeCreature always adds the CREATURE card type (Layer 4) and sets
 *     base P/T 1/1 (Layer 7b), leaving the reanimated card's printed artifact/enchantment types
 *     intact — "in addition to its other types."
 *  3. [Effects.AddSubtype] grants the **Spirit** subtype additively. BecomeCreature's `creatureTypes`
 *     parameter *replaces* all subtypes (SetCreatureSubtypes), which would strip the card's printed
 *     subtypes, so Spirit is added with a separate AddSubtype modification (same split as Relic's Roar).
 *  4. [Effects.AddDynamicCounters] puts **X** +1/+1 counters on it, where X is the spell's chosen
 *     {X} ([DynamicAmount.XValue]). Applied after it is already a creature so the counters count
 *     toward its power/toughness. Final stats: (1 + X) / (1 + X).
 *
 * The target filter accepts an artifact card, or an enchantment card that is not an Aura, owned by
 * the caster in their graveyard. Both branches share the ownedByYou gate, so the OR collapses to a
 * single CardPredicate.Or under the graveyard zone restriction.
 */
val AbuelosAwakening = card("Abuelo's Awakening") {
    manaCost = "{X}{3}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Return target artifact or non-Aura enchantment card from your graveyard to the " +
        "battlefield with X additional +1/+1 counters on it. It's a 1/1 Spirit creature with flying " +
        "in addition to its other types."

    spell {
        val t = target(
            "target artifact or non-Aura enchantment card from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Artifact.ownedByYou() or
                        GameObjectFilter.Enchantment.notSubtype(Subtype.AURA).ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.PutOntoBattlefield(t)
            .then(
                Effects.BecomeCreature(
                    target = t,
                    power = 1,
                    toughness = 1,
                    keywords = setOf(Keyword.FLYING),
                    duration = Duration.Permanent
                )
            )
            .then(Effects.AddSubtype("Spirit", target = t, duration = Duration.Permanent))
            .then(Effects.AddDynamicCounters(Counters.PLUS_ONE_PLUS_ONE, DynamicAmount.XValue, t))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "1"
        artist = "Eelis Kyttanen"
        flavorText = "Quintorius touched the old poncho he'd found. A rush of magic condensed into an " +
            "Echo, and wise Abuelo stepped forth."
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f93b725e-2b9c-4830-ac54-b2562afe09bb.jpg?1782694610"
    }
}
