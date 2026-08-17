package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Oblivion Stone — Mirrodin #222
 * {3} · Artifact
 *
 * The board wipe gathers every nonland permanent lacking a fate counter before any of them are
 * destroyed, so destruction is simultaneous. Its second step iterates only the surviving
 * fate-marked permanents and removes fate counters while preserving counters of every other kind.
 */
val OblivionStone = card("Oblivion Stone") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{4}, {T}: Put a fate counter on target permanent.\n" +
        "{5}, {T}, Sacrifice this artifact: Destroy each nonland permanent without a fate counter " +
        "on it, then remove all fate counters from all permanents."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        target = Targets.Permanent
        effect = Effects.AddCounters(Counters.FATE, 1, EffectTarget.ContextTarget(0))
        description = "{4}, {T}: Put a fate counter on target permanent."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.DestroyAll(GameObjectFilter.NonlandPermanent.withoutCounter(Counters.FATE)),
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Permanent.withCounter(Counters.FATE)),
                Effects.RemoveAllCountersOfType(Counters.FATE, EffectTarget.Self)
            )
        )
        description = "{5}, {T}, Sacrifice this artifact: Destroy each nonland permanent without " +
            "a fate counter on it, then remove all fate counters from all permanents."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "222"
        artist = "Sam Wood"
        imageUri = "https://cards.scryfall.io/normal/front/d/d/ddba1566-2778-4636-b4d3-9095fb2d83c8.jpg?1783944509"
    }
}
