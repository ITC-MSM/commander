package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Wildwood Scourge
 * {X}{G}
 * Creature — Hydra
 * 0/0
 * This creature enters with X +1/+1 counters on it.
 * Whenever one or more +1/+1 counters are put on another non-Hydra creature you control,
 * put a +1/+1 counter on this creature.
 *
 * `firstTimeEachTurn = false` — the ability watches every counter placement, not just the
 * first on a given creature each turn. Per the Scryfall ruling the Scourge gets exactly one
 * counter per event batch no matter how many counters landed, which is what
 * `countersPlacedOn` (a per-batch watcher) delivers.
 */
val WildwoodScourge = card("Wildwood Scourge") {
    manaCost = "{X}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Hydra"
    power = 0
    toughness = 0
    oracleText = "This creature enters with X +1/+1 counters on it.\n" +
        "Whenever one or more +1/+1 counters are put on another non-Hydra creature you control, " +
        "put a +1/+1 counter on this creature."

    replacementEffect(
        EntersWithDynamicCounters(
            counterType = CounterTypeFilter.PlusOnePlusOne,
            count = DynamicAmount.XValue
        )
    )

    triggeredAbility {
        trigger = Triggers.countersPlacedOn(
            filter = GameObjectFilter.Creature.youControl().notSubtype(Subtype.HYDRA),
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            firstTimeEachTurn = false,
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.AddCounters(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            count = 1,
            target = EffectTarget.Self
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "214"
        artist = "Bryan Sola"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46ff0b33-d153-4b0e-ac48-7e5ed70dea09.jpg?1783930664"
        ruling(
            "2024-11-08",
            "Abilities that trigger when counters are put on a creature trigger when a creature " +
                "enters with counters and when a player puts counters on a creature."
        )
        ruling(
            "2024-11-08",
            "Wildwood Scourge gets just one +1/+1 counter when its last ability resolves, no matter " +
                "how many counters were put on the non-Hydra creature."
        )
    }
}
