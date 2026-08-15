package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Altar of Shadows — Mirrodin #143 (canonical printing, only printing)
 * {7} · Artifact
 *
 * At the beginning of your first main phase, add {B} for each charge counter on this artifact.
 * {7}, {T}: Destroy target creature. Then put a charge counter on this artifact.
 *
 * The mana trigger is [Triggers.FirstMainPhase] — the precombat main phase, which the 2004-10-04
 * ruling is explicit about being the *first* main phase of the turn regardless of what else moved
 * around it. The amount is `countersOnSelf(charge)`, re-read each turn, so the altar ramps itself as
 * its activated ability feeds it counters.
 *
 * "Destroy target creature. Then put a charge counter" is a plain [Effects.Composite]: the counter is
 * not conditional on the destruction sticking, which is exactly the 2004-12-01 ruling ("You put the
 * counter on Altar of Shadows even if the creature regenerates").
 */
val AltarOfShadows = card("Altar of Shadows") {
    manaCost = "{7}"
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "At the beginning of your first main phase, add {B} for each charge counter on this artifact.\n" +
        "{7}, {T}: Destroy target creature. Then put a charge counter on this artifact."

    triggeredAbility {
        trigger = Triggers.FirstMainPhase
        effect = Effects.AddMana(
            Color.BLACK,
            DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.CHARGE))
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{7}"), Costs.Tap)
        val creature = target("creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.Destroy(creature),
            Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self)
        )
        description = "{7}, {T}: Destroy target creature. Then put a charge counter on this artifact."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "143"
        artist = "Sam Wood"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/ebc3824c-11ee-4fec-9397-823783b682d9.jpg?1783944528"
        ruling("2004-10-04", "The precombat main phase is the first main phase of the turn. All others are postcombat main phases, even if they technically occur before combat.")
        ruling("2004-12-01", "You put the counter on Altar of Shadows even if the creature regenerates.")
    }
}
