package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Hei Bai, Spirit of Balance
 * {2}{W/B}{W/B}
 * Legendary Creature — Bear Spirit
 * 3/3
 *
 * Whenever Hei Bai enters or attacks, you may sacrifice another creature or artifact. If you do,
 * put two +1/+1 counters on Hei Bai.
 * When Hei Bai leaves the battlefield, put its counters on target creature you control.
 *
 * "Enters or attacks" is two triggered abilities sharing one effect (CR 603.2 — the two events
 * trigger independently), each a `may sacrifice another creature or artifact -> two +1/+1 counters`
 * (the Comet Crawler / Swarm Culler shape: a `MayEffect` over `SacrificeTarget then AddCounters`).
 * The leaves-the-battlefield trigger reuses the shared `MoveAllLastKnownCounters` pattern (cf.
 * Dockworker Drone, Servant of the Scale); since Hei Bai only ever bears +1/+1 counters, moving all
 * last-known counters matches "put its counters on target creature you control".
 */
val HeiBaiSpiritOfBalance = card("Hei Bai, Spirit of Balance") {
    manaCost = "{2}{W/B}{W/B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Bear Spirit"
    power = 3
    toughness = 3
    oracleText = "Whenever Hei Bai enters or attacks, you may sacrifice another creature or artifact. " +
        "If you do, put two +1/+1 counters on Hei Bai.\n" +
        "When Hei Bai leaves the battlefield, put its counters on target creature you control."

    // Whenever Hei Bai enters, you may sacrifice another creature or artifact for two +1/+1 counters.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val sacrificeTarget = target(
            "another creature or artifact",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Creature.youControl().or(GameObjectFilter.Artifact.youControl())
                ).other()
            )
        )
        effect = MayEffect(
            Effects.SacrificeTarget(sacrificeTarget)
                then Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
        )
        description = "Whenever Hei Bai enters, you may sacrifice another creature or artifact. " +
            "If you do, put two +1/+1 counters on Hei Bai."
    }

    // Whenever Hei Bai attacks, you may sacrifice another creature or artifact for two +1/+1 counters.
    triggeredAbility {
        trigger = Triggers.Attacks
        val sacrificeTarget = target(
            "another creature or artifact",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Creature.youControl().or(GameObjectFilter.Artifact.youControl())
                ).other()
            )
        )
        effect = MayEffect(
            Effects.SacrificeTarget(sacrificeTarget)
                then Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
        )
        description = "Whenever Hei Bai attacks, you may sacrifice another creature or artifact. " +
            "If you do, put two +1/+1 counters on Hei Bai."
    }

    // When Hei Bai leaves the battlefield, put its counters on target creature you control.
    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        target = Targets.CreatureYouControl
        effect = Effects.MoveAllLastKnownCounters(EffectTarget.ContextTarget(0))
        description = "When Hei Bai leaves the battlefield, put its counters on target creature you control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Tyler Smith"
        flavorText = "\"My friend gave me hope that the forest would grow back.\"\n—Aang"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/060d24e0-1567-41f1-ae8c-2d3b1834df3c.jpg?1764121643"
    }
}
