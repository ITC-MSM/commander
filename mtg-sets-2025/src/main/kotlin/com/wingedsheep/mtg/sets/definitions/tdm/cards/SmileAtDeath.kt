package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Smile at Death — Tarkir: Dragonstorm #24
 * {3}{W}{W} · Enchantment
 *
 * At the beginning of your upkeep, return up to two target creature cards with power 2 or
 * less from your graveyard to the battlefield. Put a +1/+1 counter on each of those creatures.
 *
 * Each chosen graveyard card is returned to the battlefield and then gets a +1/+1 counter,
 * iterated per-target via [ForEachTargetEffect]. The target is optional ("up to two"); the
 * returned card keeps its entity id across the zone move, so `ContextTarget(0)` still resolves
 * to it for the counter (same pattern as Coiling Rebirth).
 */
val SmileAtDeath = card("Smile at Death") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, return up to two target creature cards with " +
        "power 2 or less from your graveyard to the battlefield. Put a +1/+1 counter on each of " +
        "those creatures."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        target = TargetObject(
            count = 2,
            optional = true,
            filter = TargetFilter.CreatureInYourGraveyard.powerAtMost(2)
        )
        effect = ForEachTargetEffect(
            effects = listOf(
                Effects.Move(
                    EffectTarget.ContextTarget(0),
                    Zone.BATTLEFIELD,
                    fromZone = Zone.GRAVEYARD
                ).then(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0)))
            )
        )
        description = "At the beginning of your upkeep, return up to two target creature cards with " +
            "power 2 or less from your graveyard to the battlefield. Put a +1/+1 counter on each of those creatures."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "24"
        artist = "Olivier Bernard"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae2da18f-0d7d-446c-b463-8bf170ed95da.jpg?1743204050"
    }
}
